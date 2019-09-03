package com.etc.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author zhw
 * @Project: etc-manager-job
 * @Package com.etc.utils
 * @Description: ${todo}
 * @date 2019/7/11 14:32
 */
public class HttpClientUtil {
    public static String send(String method,String serverUrl,String serverPath,String serverParameter){
        String response = "";
        if(HttpMethod.POST.toString().equals(method)){
            WebClient webClient = WebClient.create(serverUrl);
            if(!StringUtils.isEmpty(serverParameter)){
                response =  webClient .method(HttpMethod.POST).uri(serverPath).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(BodyInserters.fromObject(serverParameter)).exchange().block().bodyToMono(String.class).block();
            }else {
                response =  webClient .method(HttpMethod.POST).uri(serverPath).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .exchange().block().bodyToMono(String.class).block();
            }

        } else if (HttpMethod.GET.toString().equals(method)){
            String uri = serverUrl + serverPath;
            String param = "?";
            if(!StringUtils.isEmpty(serverParameter)){
                Map maps = (Map) JSON.parse(serverParameter);
                for (Object map : maps.entrySet()){
                    param = param + ((Map.Entry)map).getKey() + "=" +((Map.Entry)map).getValue() + "&";
                }
                uri = uri + param.substring(0,param.length()-1);
            }
            response = WebClient.create()
                    .method(HttpMethod.GET)
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class).block();
        }
        return response;
    };

    public static void main(String[] args){
        String parameter = "{ \t\"key\":\"ETC_API_00050\", \t\"request_no\":\"54345524234\", \t\"method\":\"selectWebSiteInfo\", \t\"charset\":\"utf-8\", \t\"timestamp\":\"2018-03-06\", \t\"data\": \"2019-07-09 19:32:23\" }";
        Map maps = (Map) JSON.parse(parameter);
        if(maps.containsKey("data")){
            Object object = maps.get("data");
        }
        //get
        //String response = send("GET","http://localhost:8083","/user","{\"name\":\"ETC_API_00048\",\"password\":\"54345524234\"}");
        String serverParameter= "{\"name\":\"ETC_API_00048\",\"password\":\"54345524234\"}";





        //System.out.println(param.substring(0,param.length()-1));
    }
}
