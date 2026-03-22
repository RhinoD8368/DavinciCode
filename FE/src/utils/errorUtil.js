export const errorUtil = {
    /** 
     * @name errorProcess
     * @param {import('axios').AxiosError} error
     * @returns void
     * @description api 호출 시 오류를 공통으로 처리하는 함수
     * @author Rhinod
     */
    errorProcess(error) {
        const status = error.response?.status;
        const message = error.response?.data?.message;

        console.error("### [errorUtil] error :: ", error);
        console.log("### [errorUtil - errorProcess] status :: ", status);
        console.log("### [errorUtil - errorProcess] message :: ", message);

        switch (status) {
            case 401:
                // alert(message);
                // window.location.href = "/";
                break;
            case 403:
                // alert("err 403");
                break;
            case 500:
            //     alert("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                break;
            default:
                // 그 외의 에러는 메시지만 띄워줌
                alert(message);
                break;
        }
    }
};