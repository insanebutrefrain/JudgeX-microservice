/**
 * 用户是否登录
 */
const isLogin = () => {
    const token = localStorage.getItem('token');
    if (!token) return false; // 未登录
    return true;
}
export default isLogin;
