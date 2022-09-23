import { apiInstance, fileApiInstance } from "./index";

const api = apiInstance();
const fileApi = fileApiInstance();

async function getFlowerKind(success, fail) {
  await api
    .get(`/api/community/article`, {
      // headers: {
      //   Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
      // },
    })
    .then(success)
    .catch(fail);
}

async function postArticle(article, success, fail) {
  await fileApi
    .post(`/api/community/article`, article)
    .then(success)
    .catch(fail);
}

export { getFlowerKind, postArticle };
