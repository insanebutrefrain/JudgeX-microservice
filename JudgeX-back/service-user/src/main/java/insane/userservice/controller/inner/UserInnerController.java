package insane.userservice.controller.inner;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import insane.client.service.UserFeignClient;
import insane.model.entity.User;
import insane.userservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 此服务仅内部调用
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {
    @Resource
    private UserService userService;

    /**
     * 获取更新链
     *
     * @return
     */
    @GetMapping("/update")
    public UpdateChainWrapper<User> update() {
        return userService.update();
    }

    /**
     * 获取用户版本
     *
     * @param userId
     * @return
     */

    @GetMapping("/get/user_avatar_version")
    public String getUserAvatarVersion(Long userId) {
        return userService.getUserAvatarVersion(userId);
    }

    /**
     * 获取所有洪湖
     */
    @GetMapping("/list/all")
    public List<User> list() {
        return userService.list();
    }

    /**
     * 根据 id 获取用户
     *
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    }

    ;

    /**
     * 根据 id 列表获取用户列表
     *
     * @param idList
     * @return
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
        return userService.listByIds(idList);
    }
}
