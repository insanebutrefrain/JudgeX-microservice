const getStatusColor = (judgeResult: string) => {
    if (!judgeResult) return 'gray';
    const colorMap: Record<string, string> = {
        'Accepted': 'green',
        'Wrong Answer': 'red',
        'Time Limit Exceeded': 'orange',
        'Memory Limit Exceeded': 'purple',
        'Output Limit Exceeded': 'orange',
        'Compile Error': 'red',
        'Runtime Error': 'red',
        'Presentation Error': 'orange',
        'System Error': 'red',
        'Waiting': 'gray'
    };
    return colorMap[judgeResult] || 'gray';
};
export default getStatusColor;
