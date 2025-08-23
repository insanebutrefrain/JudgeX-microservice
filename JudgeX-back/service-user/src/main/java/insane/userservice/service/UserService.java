package insane.userservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import insane.model.dto.user.UserQueryRequest;
import insane.model.entity.User;
import insane.model.vo.LoginUserVO;
import insane.model.vo.UserVO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 用户服务
 */
public interface UserService extends IService<User> {

    /**
     用户注册

     @param userAccount   用户账户
     @param userPassword  用户密码
     @param checkPassword 校验密码
     @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     用户登录

     @param userAccount  用户账户
     @param userPassword 用户密码
     @param request
     @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     获取当前登录用户

     @param request
     @return 未登录则报错
     */
    @NotNull
    User getLoginUser(HttpServletRequest request);

    /**
     获取当前登录用户

     @param request
     @return 未登录则返回null
     */
    @Nullable
    User getLoginUserNullable(HttpServletRequest request);


    /**
     是否为管理员

     @param request
     @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     是否为管理员

     @param user
     @return
     */
    boolean isAdmin(User user);

    /**
     用户注销

     @param request
     @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     获取脱敏的已登录用户信息

     @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     获取脱敏的用户信息

     @param user
     @return
     */
    UserVO getUserVO(User user);

    /**
     获取脱敏的用户信息

     @param userList
     @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     获取查询条件

     @param userQueryRequest
     @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     获取用户头像版本号
     */
    String getUserAvatarVersion(Long userId);

    String getUserAvatarVersionNullable(Long userId);

}
