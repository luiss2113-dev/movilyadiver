package com.desarrollosmoyan.movilyaDriver.Retrofit;


import com.desarrollosmoyan.movilyaDriver.Helper.URLHelper;
import com.desarrollosmoyan.movilyaDriver.Model.ProviderDocumentsModel;
import com.desarrollosmoyan.movilyaDriver.Model.ServiceDocumentsModel;
import com.desarrollosmoyan.movilyaDriver.Model.UpdateServiceModel;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface ApiInterface {

    @FormUrlEncoded
    @POST("api/provider/trip/{id}/calculate")
    Call<ResponseBody> getLiveTracking(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                       @Path("id") String id,
                                       @Field("latitude") String latitude, @Field("longitude") String longitude);


    //document apis

    @GET(URLHelper.GET_DRIVER_DOCUMENTS)
    Single<ServiceDocumentsModel> getServiceDocuments(@Header("Authorization") String srtToken);

    @Multipart
    @POST(URLHelper.UPLOAD_DRIVER_DOCUMENTS)
    Single<ProviderDocumentsModel> uploadDriverDocument(@Header("Authorization") String token,
                                                        @Part MultipartBody.Part document,
                                                        @Part("document_id") int documentId);
    @FormUrlEncoded
    @POST(URLHelper.UPDATE_DRIVER_DOCUMENTS)
    Single<UpdateServiceModel> updateService(@Header("Authorization") String token,
                                             @Field("service_type") int serviceTypeId,
                                             @Field("service_number") String serviceNumber,
                                             @Field("service_model") String serviceModel);
}
