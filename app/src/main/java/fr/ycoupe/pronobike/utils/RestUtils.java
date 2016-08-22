package fr.ycoupe.pronobike.utils;

import fr.ycoupe.pronobike.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Utility class to create rest adapters for API calls.
 */
public class RestUtils {

    /**
     * Create a RestAdapter used for calls on APis.
     *
     * @param baseUrl The base url.
     * @return A RestAdapter with specific log level.
     */
    public static Retrofit makeAdapter(final String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpClientToInterceptLog())
                .build();
    }

    private static OkHttpClient getOkHttpClientToInterceptLog() {
        //Adding log interceptor
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        return httpClient.build();
    }
}
