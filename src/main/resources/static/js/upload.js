async function uploadToServer(formObj){

    console.log("upload to server.......")
    console.log(formObj)

    const response = await axios({
        method: 'post',
        url: '/upload',
        data: formObj,
        header: {
            'Content-Type': 'multipart/form-data',
        },
    });

    return response.data

}

async function removeFileToServer(uuid, fileName){

    const response = await axios.delete(`/remove/${uuid}_${fileName}`)
    return response.data

}

async function IngredientToServer(formObj){

    const response = await axios.post('/recipes', ingredients)
        .then(response => {
            // Handle success
            console.log(response.data);
        })
        .catch(error => {
            // Handle error
            console.error(error);
        });

    return response.data;


}


