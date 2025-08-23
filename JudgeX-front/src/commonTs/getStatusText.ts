const getStatusText = (judgeResult: string) => {
    if (!judgeResult) return '等待中';
    const statusMap: Record<string, string> = {
        'Accepted': '答案正确',
        'Wrong Answer': '答案错误',
        'Time Limit Exceeded': '运行超时',
        'Memory Limit Exceeded': '内存超限',
        'Output Limit Exceeded': '输出超限',
        'Compile Error': '编译错误',
        'Runtime Error': '运行错误',
        'Presentation Error': '输出格式错误',
        'System Error': '系统错误',
        'Waiting': '等待中'
    };
    return statusMap[judgeResult] || judgeResult;
};
export default getStatusText;
