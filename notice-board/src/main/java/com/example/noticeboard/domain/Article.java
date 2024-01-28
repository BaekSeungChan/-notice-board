package com.example.noticeboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Setter @Column(nullable = false) private String title; // 제목
    @Setter @Column(nullable = false, length = 10000) private String content;  // 본문

    @Setter private String hashtag; // 해시태그

    @ToString.Exclude
    @OrderBy("id")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL )
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();
    // article에 연동되어 잇는 comment는 중복을 허용하지 않고 다 여기에 모아서 Collection으로 보겠다.
    // 양반향 바인딩

    @CreatedDate @Column(nullable = false) private LocalDateTime createAt; //생성일시
    @CreatedBy @Column(nullable = false, length = 100)private String createdBy; // 생성자
    @LastModifiedDate @Column(nullable = false)private LocalDateTime modifiedAt; // 수정일시
    @LastModifiedBy @Column(nullable = false, length = 100) private String modifiedBy; // 수정자


    protected Article() {}

    private Article(String title, String content, String hashtag){
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(String title, String content, String hashtag){
        return new Article(title, content, hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return id != null && id.equals(article.id);
    }

    // 왜 id만 설정했는가?
    // 엔티티를 데이터베이스에 영속화 시키고 연결지으고 사용하는 환경에서 서로 다른 두 로우가
    // 두 엔티티가 같은 조건이 무엇인가에 대한 질문에 지금 이 equals가 답을 하고 있다.
    // id가 부여되지 않았다, 즉 영속화되지 않았다고 하면 동등성 검사 자체가 의미가 없는 걸로 보고 다 다른 것으로
    // 간주하거나 혹은 처리하지 않겠다는 뜻입니다.
    // 그래서 false 처리해버리고 이제 id가 잇다면 id가 같은지만 보고 id가 같다면 당연히 두 객체는 같은 객체다
    // 그걸 통해서 동등성 검사를 한다.

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
