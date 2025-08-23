/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponse_Array_byte_ } from '../models/BaseResponse_Array_byte_';
import type { BaseResponse_boolean_ } from '../models/BaseResponse_boolean_';
import type { BaseResponse_List_JudgeCase_ } from '../models/BaseResponse_List_JudgeCase_';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class FileRecordControllerService {
    /**
     * getAvatar
     * @returns BaseResponse_Array_byte_ OK
     * @throws ApiError
     */
    public static getAvatarUsingGet(): CancelablePromise<BaseResponse_Array_byte_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/file/get/avatar',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getJudgecase
     * @param questionId questionId
     * @returns BaseResponse_List_JudgeCase_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static getJudgecaseUsingPost(
        questionId: number,
    ): CancelablePromise<BaseResponse_List_JudgeCase_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/file/get/judgecase',
            query: {
                'questionId': questionId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * uploadAvatar
     * @param file
     * @returns BaseResponse_boolean_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static uploadAvatarUsingPost(
        file?: Blob,
    ): CancelablePromise<BaseResponse_boolean_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/file/upload/avatar',
            formData: {
                'file': file,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * uploadJudgecase
     * @param questionId questionId
     * @param outputFiles
     * @param inputFiles
     * @returns BaseResponse_boolean_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static uploadJudgecaseUsingPost(
        questionId: number,
        outputFiles?: Array<Blob>,
        inputFiles?: Array<Blob>,
    ): CancelablePromise<BaseResponse_boolean_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/file/upload/judgecase',
            query: {
                'questionId': questionId,
            },
            formData: {
                'outputFiles': outputFiles,
                'inputFiles': inputFiles,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
