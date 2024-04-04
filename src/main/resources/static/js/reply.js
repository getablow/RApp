
//비동기 함수
async function get1(rid){

    const result = await axios.get(`/api/recipeReplies/main/${rid}`) //replyController url 사용해라

    return result;
}

async function getList({rid, page, size, goLast}){

    const result = await axios.get(`/api/recipeReplies/main/${rid}`, {params: {page, size}})

    if(goLast){
        const total = result.data.total
        const lastPage = parseInt(Math.ceil(total/size))
        return getList({rid:rid, page:lastPage, size:size})
    }

    return result.data
}

async function addReply(replyObj){
    const response = await axios.post(`/api/recipeReplies/`, replyObj)
    return response.data
}

async function getReply(rno){
    const response = await axios.get(`/api/recipeReplies/${rno}`)
    return response.data
}

async function modifyReply(replyObj){
    const response = await axios.put(`/api/recipeReplies/${replyObj.rno}`, replyObj)
    return response.data
}

async function removeReply(rno){
    const response = await axios.delete(`/api/recipeReplies/${rno}`)
    return response.data
}