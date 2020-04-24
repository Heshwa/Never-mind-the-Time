package heshwa.nevermind_thetime;

import heshwa.nevermind_thetime.Notifications.MyResponse;
import heshwa.nevermind_thetime.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static heshwa.nevermind_thetime.R.string.Authorization_key;

public interface APIService
{
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAptbF2HY:APA91bH7zvfvDysZe2kp3YmzLU5XdKSYeJ5JqmBWqVjw7RxvK8pA3u_WbR0uOTGA_0VCgTVnniRJrvu4o-9UQmHVYo-dtxkYE6FTW7OvItSWWyFGdtV-GLJ64ct544Y5MecvW6_DPzCG"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
