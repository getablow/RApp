package org.zerock.recipe.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"imageSet", "ingredientSet"})
public class Recipe extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rid;

    @Column(length = 500, nullable = false) //컬럼의 길이와 null허용여부
    private String title;

    @Column(length = 1000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    @Column(length = 500)
    private String videoUrl;

    public void change(String title, String content, String videoUrl){
        this.title = title;
        this.content = content;
        this.videoUrl = videoUrl;
    }

    @OneToMany(mappedBy = "recipe", //Image의 recipe변수. 매핑테이블 생성하지않기위해 지정.
            cascade = {CascadeType.ALL}, //영속성 전이 - All: 상위의 엔티티의 상태변경이 하위에도 적용
            fetch = FetchType.LAZY,
            orphanRemoval = true) //하위 엔티티의 참조가 없는 상태가되면 데이터 아예삭제되게함 true
    @Builder.Default
    @BatchSize(size = 20) //N+1 해결방법, 하나하나 조회하는 것보다
    private Set<RecipeImage> imageSet = new HashSet<>();



    //jpaRepository 따로 생성하지 않고 하위엔티티 객체들을 관리하는 addImage와 clearImages 메소드
    public void addImage(String uuid, String fileName){

        RecipeImage recipeImage = RecipeImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .recipe(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(recipeImage);
    }

    public void clearImages() {

        imageSet.forEach(boardImage -> boardImage.changeRecipe(null));

        this.imageSet.clear();
    }


    @OneToMany(mappedBy = "recipe",
            cascade = {CascadeType.ALL}, //상위엔티티 변경 시 하위 객체 같이 변경 됨
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<RecipeIngredient> ingredientSet = new HashSet<>();


    public void addIngredients(String name, String amount){

        RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                .name(name)
                .recipe(this)
                .amount(amount)
                .build();
        ingredientSet.add(recipeIngredient);
    }

    public void clearIngredients() {

        ingredientSet.forEach(recipeIngredient -> recipeIngredient.changeRecipe(null));

        this.ingredientSet.clear();
    }

}
