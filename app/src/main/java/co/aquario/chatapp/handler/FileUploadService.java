package co.aquario.chatapp.handler;


import co.aquario.chatapp.model.UploadCallback;
import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface FileUploadService {


    @Multipart
    @POST("/upload")
    void upload(@Part("file[]") TypedFile file,
                @Part("description") String description,
                Callback<UploadCallback> cb);
}
