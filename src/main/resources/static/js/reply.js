
//비동기 함수
async function get1(bno){

    const result = await axios.get(`/api/replies/list/${bno}`) //replyController url 사용해라

    //console.log(result)

    return result;
}

async function getList({bno, page, size, goLast}){

    const result = await axios.get(`/api/replies/list/${bno}`, {params: {page, size}})

    return result.data
}