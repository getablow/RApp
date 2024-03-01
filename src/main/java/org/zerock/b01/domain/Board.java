package org.zerock.b01.domain;

import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet")
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(length = 500, nullable = false) //컬럼의 길이와 null허용여부
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    public void change(String title, String content){
        this.title = title;
        this.content = content;
    }

    @OneToMany(mappedBy = "board", //boardImage의 board변수. 매핑테이블 생성하지않기위해 지정.
                cascade = {CascadeType.ALL}, //영속성 전이 - All: 상위의 엔티티의 상태변경이 하위에도 적용
                fetch = FetchType.LAZY,
                orphanRemoval = true) //하위 엔티티의 참조가 없는 상태가되면 데이터 아예삭제되게함 true
    @Builder.Default
    @BatchSize(size = 20) //N+1 해결방법, 하나하나 조회하는 것보다
    private Set<BoardImage> imageSet = new HashSet<>();



    //jpaRepository 따로 생성하지 않고 하위엔티티 객체들을 관리하는 addImage와 clearImages 메소드
    public void addImage(String uuid, String fileName){

        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }

    public void clearImages() {

        imageSet.forEach(boardImage -> boardImage.changeBoard(null));

        this.imageSet.clear();
    }

}
