package tom.eyre.mp2021.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import tom.eyre.mp2021.data.ExpenseCalculatedResponse;
import tom.eyre.mp2021.data.ExpenseResponse;
import tom.eyre.mp2021.data.ExpenseType;
import tom.eyre.mp2021.data.ExpenseTypesByYear;
import tom.eyre.mp2021.data.ExpensesByYear;
import tom.eyre.mp2021.database.local.LocalDatabase;
import tom.eyre.mp2021.entity.ExpenseEntity;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.utility.GetCsvUtil;

import static tom.eyre.mp2021.utility.Constants.SEVEN_DAYS;

public class ExpenseService {

    private static final Logger log = Logger.getLogger(String.valueOf(ExpenseService.class));
    private static final String URL = "http://www.theipsa.org.uk";
    private static final String COSTS = "/mp-costs/your-mp/";

    private static final String MP_COST_ID_SEARCH = "\"MpSummaries\":[{\"Id\":";

    @SneakyThrows
    public JSONArray getMembersOfParliament(MpEntity mp) {
        Integer costsId = getCostsId(URL + COSTS + mp.getForename().trim() + "-" + mp.getSurname().trim());
        if(costsId == -1){costsId = getCostsId(URL + COSTS + mp.getFullName().trim().replaceAll(" +", " ").replaceAll(" ", "-"));}
        if(costsId == -1){return new JSONArray();}
        String url = "https://www.theipsa.org.uk/download/downloadclaimscsvformp/" + costsId;
        log.info(url);
        JSONArray jsonArray = getJsonFromCsv(new GetCsvUtil().getJSONFromUrl(url));

        return jsonArray;
    }

    public List<ExpenseResponse> getExpensesForMp(MpEntity mp, LocalDatabase localDatabase) throws JsonProcessingException {
        if(haveCurrentExpenses(mp.getId(), localDatabase)) return localDatabase.localDatabaseDao().getExpensesByMpId(mp.getId()).stream().map(expenseEntity -> buildExpenseResponse(expenseEntity)).collect(Collectors.toList());
        JSONArray jsonArray = getMembersOfParliament(mp);
        try {
            for(int i = jsonArray.length() - 1; i > 0; i--){
                if(jsonArray.getJSONObject(i).getString("Year").equalsIgnoreCase("")) {jsonArray.remove(i);}
            }
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<ExpenseResponse> expenseResponses = mapper.readValue(jsonArray.toString(), new TypeReference<ArrayList<ExpenseResponse>>() { });
            buildAndSaveEntities(expenseResponses,localDatabase,mp.getId());
            return expenseResponses;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ExpensesByYear> buildYearlyList(List<ExpenseResponse> expenseResponses){
        List<ExpensesByYear> expensesByYearResponse = new ArrayList<>();
        for(ExpenseResponse expenseResponse : expenseResponses){
            String currentYear = expenseResponse.getYear() instanceof String ? "20" + ((String) expenseResponse.getYear()).substring(0, 2) : null;
            List<ExpenseResponse> currentExpenseList = new ArrayList<>();
            ExpensesByYear expensesByYear = new ExpensesByYear();
            if(expensesByYearResponse.isEmpty()){
                currentExpenseList.add(expenseResponse);
                expensesByYear.setExpenses(currentExpenseList);
                expensesByYear.setYear(currentYear);
                expensesByYearResponse.add(expensesByYear);
            } else {
                for(int i = 0; i < expensesByYearResponse.size(); i++){
                    if (currentYear.equalsIgnoreCase(expensesByYearResponse.get(i).getYear())) {
                        expensesByYearResponse.get(i).getExpenses().add(expenseResponse);
                        break;
                    } else if (i == expensesByYearResponse.size() - 1){
                        currentExpenseList.add(expenseResponse);
                        expensesByYear.setExpenses(currentExpenseList);
                        expensesByYear.setYear(currentYear);
                        expensesByYearResponse.add(expensesByYear);
                    }
                }
            }
        }
        return expensesByYearResponse;
    }

    private ExpenseResponse buildExpenseResponse(ExpenseEntity expenseEntity){
        ExpenseResponse expenseResponse = new ExpenseResponse();
        expenseResponse.setAmountPaid(expenseEntity.getAmountPaid());
        expenseResponse.setStatus(expenseEntity.getStatus());
        expenseResponse.setAmountClaimed(expenseEntity.getAmountClaimed());
        expenseResponse.setSupplyMonth(expenseEntity.getSupplyMonth());
        expenseResponse.setCategory(expenseEntity.getCategory());
        expenseResponse.setMileage(expenseEntity.getMileage());
        expenseResponse.setClaimNo(expenseEntity.getClaimNo());
        expenseResponse.setAmountNotPaid(expenseEntity.getAmountNotPaid());
        expenseResponse.setNights(expenseEntity.getNights());
        expenseResponse.setExpenseType(expenseEntity.getExpenseType());
        expenseResponse.setSupplyPeriod(expenseEntity.getSupplyPeriod());
        expenseResponse.setTraveledFrom(expenseEntity.getFrom());
        expenseResponse.setMpsConstituency(expenseEntity.getMpsConstituency());
        expenseResponse.setAmountRepaid(expenseEntity.getAmountRepaid());
        expenseResponse.setDate(expenseEntity.getDate());
        expenseResponse.setTravelClass(expenseEntity.getTravel());
        expenseResponse.setReasonIfNotPaid(expenseEntity.getReasonIfNotPaid());
        expenseResponse.setDetails(expenseEntity.getDetails());
        expenseResponse.setJourneyType(expenseEntity.getJourneyType());
        expenseResponse.setYear(expenseEntity.getYear());
        expenseResponse.setShortDescription(expenseEntity.getShortDescription());
        expenseResponse.setMpsName(expenseEntity.getMpName());
        expenseResponse.setTraveledTo(expenseEntity.getTo());
        return expenseResponse;
    }

    private Boolean haveCurrentExpenses(Integer id, LocalDatabase localDatabase){
        Long expenseDate = localDatabase.localDatabaseDao().getExpenseDate(id);
        return expenseDate != null ? System.currentTimeMillis() - expenseDate < SEVEN_DAYS : false;
    }

    public ExpenseCalculatedResponse calculateExpenseEntities(List<ExpenseEntity> expenses){
        ExpenseCalculatedResponse response = new ExpenseCalculatedResponse();
        for(ExpenseEntity expense : expenses){
            if(responseContainsExpenseYearAlready(response, expense)){
                for(ExpenseTypesByYear expenseByYear : response.getExpenseTypesByYears()){
                    if(expenseByYear.getYear().equalsIgnoreCase(expense.getYear() instanceof String ? expense.getYear() : "")){
                        if(expenseYearContainsExpenseTypeAlready(expenseByYear.getExpenseTypes(),expense.getExpenseType())){
                            for(ExpenseType type : expenseByYear.getExpenseTypes()){
                                if(type.getType().equalsIgnoreCase(expense.getExpenseType()) && !expense.getExpenseType().equalsIgnoreCase("payroll")){
                                    type.setTotalSpent(type.getTotalSpent() + expense.getAmountPaid());
                                }
                            }
                        }else if(!expense.getExpenseType().equalsIgnoreCase("payroll")){
                            expenseByYear.getExpenseTypes().add(new ExpenseType(expense.getExpenseType(), expense.getAmountPaid()));
                        }
                    }
                }
            }else if(!expense.getExpenseType().equalsIgnoreCase("payroll")){
                List<ExpenseType> expenseTypes = new ArrayList<>();
                expenseTypes.add(new ExpenseType(expense.getExpenseType(), expense.getAmountPaid()));
                response.getExpenseTypesByYears().add(new ExpenseTypesByYear(expense.getYear() instanceof String ? expense.getYear() : "", expenseTypes));
            }
        }

        return response;
    }

    private void buildAndSaveEntities(ArrayList<ExpenseResponse> responses, LocalDatabase localDatabase, Integer id){
        ArrayList<ExpenseEntity> expenseEntities = new ArrayList<>();
        for(ExpenseResponse response : responses){
            ExpenseEntity expenseEntity = new ExpenseEntity();
            expenseEntity.setMpId(id);
            expenseEntity.setYear(response.getYear() instanceof String ? response.getYear().toString() : "");
            expenseEntity.setDate(response.getDate());
            expenseEntity.setClaimNo(response.getClaimNo());
            expenseEntity.setMpName(response.getMpsName());
            expenseEntity.setMpsConstituency(response.getMpsConstituency());
            expenseEntity.setCategory(response.getCategory());
            expenseEntity.setExpenseType(response.getExpenseType());
            expenseEntity.setShortDescription(response.getShortDescription());
            expenseEntity.setDetails(response.getDetails());
            expenseEntity.setJourneyType(response.getJourneyType());
            expenseEntity.setFrom(response.getTraveledFrom());
            expenseEntity.setTo(response.getTraveledTo());
            expenseEntity.setTravel(response.getTravelClass());
            expenseEntity.setNights(response.getNights());
            expenseEntity.setMileage(response.getMileage());
            expenseEntity.setAmountClaimed(response.getAmountClaimed());
            expenseEntity.setAmountPaid(response.getAmountPaid());
            expenseEntity.setAmountRepaid(response.getAmountRepaid());
            expenseEntity.setStatus(response.getStatus());
            expenseEntity.setReasonIfNotPaid(response.getReasonIfNotPaid());
            expenseEntity.setSupplyMonth(response.getSupplyMonth());
            expenseEntity.setSupplyPeriod(response.getSupplyPeriod());
            expenseEntity.setLastUpdatedTimestamp(new Date().getTime());
            expenseEntities.add(expenseEntity);
        }
        localDatabase.localDatabaseDao().insertAllExpenses(expenseEntities);
    }

    public ExpenseCalculatedResponse calculateExpenses(List<ExpenseResponse> expenses){
        ExpenseCalculatedResponse response = new ExpenseCalculatedResponse();
        for(ExpenseResponse expense : expenses){
            if(responseContainsExpenseYearAlready(response, expense)){
                for(ExpenseTypesByYear expenseTypesByYear : response.getExpenseTypesByYears()){
                    if(expenseTypesByYear.getYear().equalsIgnoreCase(expense.getYear() instanceof String ? expense.getYear().toString() : "")) {
                        if (expenseYearContainsExpenseTypeAlready(expenseTypesByYear.getExpenseTypes(), expense.getExpenseType())) {
                            for (ExpenseType type : expenseTypesByYear.getExpenseTypes()) {
                                if (type.getType().equalsIgnoreCase(expense.getExpenseType()) /*&& !expense.getExpenseType().equalsIgnoreCase("payroll")*/) {
                                    type.setTotalSpent(type.getTotalSpent() + expense.getAmountPaid());
                                }
                            }
                        } else {//if (!expense.getExpenseType().equalsIgnoreCase("payroll")) {
                            expenseTypesByYear.getExpenseTypes().add(new ExpenseType(expense.getExpenseType(), expense.getAmountPaid()));
                        }
                    }
                }
            }else {//if(!expense.getExpenseType().equalsIgnoreCase("payroll")) {
                List<ExpenseType> expenseTypes = new ArrayList<>();
                expenseTypes.add(new ExpenseType(expense.getExpenseType(), expense.getAmountPaid()));
                response.getExpenseTypesByYears().add(new ExpenseTypesByYear(expense.getYear() instanceof String ? expense.getYear().toString() : "", expenseTypes));
            }
        }

        return response;
    }

    private Boolean expenseYearContainsExpenseTypeAlready(List<ExpenseType> types, String typeString){
        for(ExpenseType type : types){
            if(type.getType().equalsIgnoreCase(typeString)){
                return true;
            }
        }
        return false;
    }

    private Boolean responseContainsExpenseYearAlready(ExpenseCalculatedResponse response, ExpenseEntity expense){
        for(ExpenseTypesByYear expenseTypesByYear : response.getExpenseTypesByYears()){
            if(expenseTypesByYear.getYear() != null &&
                    expense.getYear() != null &&
                    expenseTypesByYear.getYear().equalsIgnoreCase(expense.getYear() instanceof String ? expense.getYear().toString() : "")){
                return true;
            }
        }
        return false;
    }


    private Boolean responseContainsExpenseYearAlready(ExpenseCalculatedResponse response, ExpenseResponse expense){
        for(ExpenseTypesByYear expenseTypesByYear : response.getExpenseTypesByYears()){
            if(expenseTypesByYear.getYear() != null &&
            expense.getYear() != null &&
            expenseTypesByYear.getYear().equalsIgnoreCase(expense.getYear() instanceof String ? expense.getYear().toString() : "")){
                return true;
            }
        }
        return false;
    }

    private JSONArray getJsonFromCsv(String csvString){
        try {
            CsvSchema csv = CsvSchema.emptySchema().withHeader();
            CsvMapper csvMapper = new CsvMapper();
            MappingIterator<Map<?, ?>> mappingIterator =  csvMapper.reader().forType(Map.class).with(csv).readValues(csvString.replaceAll("ï»¿","").replaceAll("\\uFEFF", "").replaceAll("\\u0000",""));
            List<Map<?, ?>> list = mappingIterator.readAll();
            return new JSONArray(list);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getCostsId(String url) {

        int retries = 1;
        while (retries-- > 0) {
            try {
                Document doc = Jsoup.connect(url).timeout(90000).get();
                Element el =  doc.select("span#expenses-panel-download-databtn").next("script").first();
                int index = el.data().indexOf(MP_COST_ID_SEARCH);
                int indexEnd = el.data().indexOf(",", index);
                String id = el.data().substring(index +  MP_COST_ID_SEARCH.length(), indexEnd);
                return Integer.parseInt(id);
            } catch (IOException i) {
                i.printStackTrace();
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                // log exception?
            }
        }

        return -1;
    }
}
