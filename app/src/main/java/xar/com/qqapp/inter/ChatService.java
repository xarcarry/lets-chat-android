package xar.com.qqapp.inter;


import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xar.com.qqapp.bean.Message;
import xar.com.qqapp.bean.ServerResult;

public interface ChatService {

    @POST("chat/sendMsg")
    Observable<ServerResult> sendMsg(@Body Message msg);
}
