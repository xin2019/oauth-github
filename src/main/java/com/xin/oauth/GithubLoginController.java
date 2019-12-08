package com.xin.oauth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Github 登录 前置控制器
 *
 * @author java997.com
 * @since 2019-05-25
 */
@Controller
@RequestMapping("/account/github")
public class GithubLoginController {
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String githubLogin() {
        String githubState = "adgasgdsdhgi";
        return "redirect:https://github.com/login/oauth/authorize?client_id=8153d4e6cb48a106b568&redirect_uri=http://127.0.0.1:8080/account/github/callback&state=" + githubState;
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    @ResponseBody
    public String githubCallback(String code, String state) {
        System.out.println("==>state:" + state);
        System.out.println("==>code:" + code);

        // Step2：通过 Authorization Code 获取 AccessToken
        String githubAccessTokenResult = HttpUtil.sendGet("https://github.com/login/oauth/access_token",
                "client_id=8153d4e6cb48a106b568&client_secret=be102d1dafd70edc37ef8ea29178b205fe8602f8&code=" + code +
                        "&redirect_uri=http://127.0.0.1:8080/account/github/callback");

        System.out.println("==>githubAccessTokenResult: " + githubAccessTokenResult);

        /**
         * 以 & 为分割字符分割 result
         */
        String[] githubResultList = githubAccessTokenResult.split("&");
        List<String> params = new ArrayList<>();

        // paramMap 是分割后得到的参数对, 比说 access_token=ad5f4as6gfads4as98fg
        for (String paramMap : githubResultList) {
            if (!"scope".equals(paramMap.split("=")[0])){
                // 再以 = 为分割字符分割, 并加到 params 中
                params.add(paramMap.split("=")[1]);
            }
        }

        //此时 params.get(0) 为 access_token;  params.get(1) 为 token_type

        // Step2：通过 access_token 获取用户信息
        String githubInfoResult = HttpUtil.sendGet("https://api.github.com/user","access_token="+params.get(0));

        return githubInfoResult;
    }

}
