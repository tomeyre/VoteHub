package tom.eyre.mp2021.utility;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DrawableFromUrl {

    private static final String IMAGE_URI = "https://data.parliament.uk/membersdataplatform/services/images/MemberPhoto/";
    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static Drawable get(Integer id) throws IOException {
        Future<Bitmap> future = executorService.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                Bitmap x;
                HttpURLConnection connection = (HttpURLConnection) new URL(IMAGE_URI + id + "/Web/").openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();

                x = BitmapFactory.decodeStream(input);
                return x;
            }
        });
        try {
            return new BitmapDrawable(Resources.getSystem(), future.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
