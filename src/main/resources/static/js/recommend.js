$(document).ready(function() {
    $('#recommendButton').click(function() {
        console.log("Button clicked. Sending AJAX request.");
        $.ajax({
            url: '/api/recipes/recommend',
            type: 'GET',
            success: function(response) {
                console.log("AJAX request succeeded. Response:", response);
                displayRecipes(response);
            },
            error: function(xhr, status, error) {
                console.error("AJAX request failed. Status:", status, "Error:", error);
                alert("Error: " + xhr.responseText);
            }
        });
    });
});

function displayRecipes(recipes) {
    var recipeList = $('#recipeList');
    recipeList.empty();

    if (recipes.length === 0) {
        recipeList.append('<p>No recipes found.</p>');
    } else {
        var ul = $('<ul>');
        recipes.forEach(function(recipe) {
            var li = $('<li>').text(recipe.title);
            var details = $('<div>').html(`
                <p><strong>Title:</strong> ${recipe.title}</p>
                <p><strong>Content:</strong> ${recipe.content}</p>
                <p><strong>Writer:</strong> ${recipe.writer}</p>
                <p><strong>View Count:</strong> ${recipe.viewCount}</p>
                <p><strong>Favorite Count:</strong> ${recipe.favoriteCount}</p>
                <p><strong>Ingredients:</strong></p>
                <ul>
                    ${recipe.ingredients.map(ingredient => `<li>${ingredient.name}: ${ingredient.amount}</li>`).join('')}
                </ul>
            `);
            li.append(details);
            ul.append(li);
        });
        recipeList.append(ul);
    }
}
