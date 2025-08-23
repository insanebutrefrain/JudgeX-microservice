import AccessEnum from "@/access/accessEnum";

/**
 * 检查当登录用户loginUser是否有needAccess权限
 * @param loginUser 当前登录用户
 * @param needAccess 需要的权限(默认未登录)
 */
const checkAccess = (loginUser: any, needAccess: any = AccessEnum.NOT_LOGIN) => {
    // 不需要权限
    if (needAccess == AccessEnum.NOT_LOGIN) return true;
    const loginUserAccess = loginUser?.userRole ?? AccessEnum.NOT_LOGIN;
    // 需要登录
    if (needAccess == AccessEnum.USER) {
        return loginUserAccess != AccessEnum.NOT_LOGIN;
    }
    // 管理员
    if (needAccess == AccessEnum.ADMIN) {
        return loginUserAccess == AccessEnum.ADMIN;
    }
    return false;
}
export default checkAccess;
