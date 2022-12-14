package com.ssafy.api.controller;

import com.ssafy.api.request.*;
import com.ssafy.api.response.Community.*;
import com.ssafy.api.service.CommunityService;
import com.ssafy.api.service.PopularService;
import com.ssafy.api.service.UserService;
import com.ssafy.common.util.JwtTokenUtil;
import com.ssafy.db.entity.Article;
import com.ssafy.db.entity.Comment;
import com.ssafy.db.entity.Subject;
import com.ssafy.db.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "커뮤니티 api", tags = {"Community"})
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    CommunityService communityService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    UserService userService;
    @Autowired
    PopularService popularService;

    @PostMapping("/article")
    @ApiOperation(value = "게시글 생성", notes = "생성된 게시글 id 값을 응답한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "게시글 생성 성공"),
            @ApiResponse(code = 401, message = "로그인 필요")
    })
    public ResponseEntity<? extends ArticlePostRes> createArticle(@RequestPart(value = "articleInfo") ArticleCreatePostReq articleInfo,
                                                                  @RequestPart(value = "images") List<MultipartFile> multipartFiles,
                                                                  HttpServletRequest request){

        String jwtToken = request.getHeader("Authorization");

        if(jwtToken == null){
            return ResponseEntity.status(401).body(ArticlePostRes.of(401, "로그인이 필요합니다.", null));
        }

        String email = jwtTokenUtil.getUserEmailFromToken(jwtToken);

        Article article = communityService.createArticle(email, articleInfo, multipartFiles);

        return ResponseEntity.status(201).body(ArticlePostRes.of(201, "정상적으로 작성되었습니다", article.getId()));
    }

    @GetMapping("/article")
    @ApiOperation(value = "품목 리스트 조회", notes = "꽃 품목 리스트를 반환한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "품목리스트 조회 성공"),
            @ApiResponse(code = 404, message = "품목리스트 조회 실패")
    })
    public ResponseEntity<? extends SubjectGetRes> getSubjectList(){

        List<Subject> subjects = communityService.getSubjects();

        if(subjects == null){
            return ResponseEntity.status(404).body(SubjectGetRes.of(404, "품목리스트 조회 실패", null));
        }else{
            return ResponseEntity.status(201).body(SubjectGetRes.of(201, "정상적으로 작성되었습니다", subjects));
        }
    }

    @PutMapping("/article/{articleId}")
    @ApiOperation(value = "게시글 수정", notes = "수정된 게시글 id 값을 응답한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "게시글 수정 성공"),
            @ApiResponse(code = 403, message = "게시글 수정 실패"),
            @ApiResponse(code = 401, message = "로그인 필요")
    })
    public ResponseEntity<? extends ArticlePostRes> updateArticle(@PathVariable("articleId") Long articleId,
                                                                  @RequestPart(value = "articleInfo") ArticleCreatePostReq articleInfo,
                                                                  @RequestPart(value = "images", required = false) List<MultipartFile> multipartFiles,
                                                                  HttpServletRequest request){

        String jwtToken = request.getHeader("Authorization");

        if(jwtToken == null){
            return ResponseEntity.status(401).body(ArticlePostRes.of(401, "로그인이 필요합니다.", 0L));
        }

        String email = jwtTokenUtil.getUserEmailFromToken(jwtToken);

        Article article = communityService.updateArticle(email, articleId, articleInfo, multipartFiles);

        if(article == null){
            return ResponseEntity.status(403).body(ArticlePostRes.of(403, "수정을 할 수 없습니다..", 0L));
        }else{
            return ResponseEntity.status(201).body(ArticlePostRes.of(201, "정상적으로 수정되었습니다", article.getId()));
        }
    }

    @DeleteMapping("/article/{articleId}")
    @ApiOperation(value = "게시글 삭제", notes = "삭제된 게시글 id 값을 응답한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "게시글 삭제 성공"),
            @ApiResponse(code = 403, message = "게시글 삭제 실패"),
            @ApiResponse(code = 401, message = "로그인 필요")
    })
    public ResponseEntity<? extends ArticleDelRes> deleteArticle(@PathVariable("articleId") Long articleId,
                                                                 HttpServletRequest request){

        String jwtToken = request.getHeader("Authorization");

        if(jwtToken == null){
            return ResponseEntity.status(401).body(ArticleDelRes.of(401, "로그인이 필요합니다.", false));
        }

        String email = jwtTokenUtil.getUserEmailFromToken(jwtToken);

        boolean isSuccess = communityService.deleteArticle(email, articleId);

        if(isSuccess){
            return ResponseEntity.status(201).body(ArticleDelRes.of(201, "정상적으로 삭제되었습니다", isSuccess));
        }else{
            return ResponseEntity.status(403).body(ArticleDelRes.of(403, "삭제할 수 없습니다.", false));
        }
    }

    @GetMapping("/article/{articleId}")
    @ApiOperation(value = "게시글 상세 조회", notes = "게시글 상세 내용을 반환한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "게시글 상세 조회 성공"),
            @ApiResponse(code = 404, message = "게시글 상세 조회 실패"),
            @ApiResponse(code = 401, message = "로그인 필요")
    })
    public ResponseEntity<? extends ArticleGetRes> getArticleDetail(@PathVariable("articleId") Long articleId,
                                                                    HttpServletRequest request){

        String jwtToken = request.getHeader("Authorization");

        if(jwtToken == null){
            return ResponseEntity.status(401).body(ArticleGetRes.of(401, "로그인이 필요합니다.", null, false, false, null));
        }

        String email = jwtTokenUtil.getUserEmailFromToken(jwtToken);

        Article article = communityService.getArticle(articleId);
        User user = userService.getUserByEmail(email);

        boolean isFollow = userService.checkFollow(user, article.getUser());

        boolean isLike = communityService.checkLike(email, articleId);

        if(article != null){
            return ResponseEntity.status(201).body(ArticleGetRes.of(201, "정상적으로 조회되었습니다", article, isFollow, isLike, user));
        }else{
            return ResponseEntity.status(404).body(ArticleGetRes.of(404, "조회에 실패하였습니다.", null, false, false, null));
        }
    }

    @GetMapping("/article/list")
    @ApiOperation(value = "게시글 목록 조회", notes = "게시글 목록를 반환한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "게시글 목록 조회 성공"),
            @ApiResponse(code = 404, message = "게시글 목록 조회 실패"),
            @ApiResponse(code = 401, message = "로그인 필요")
    })
    public ResponseEntity<? extends ArticlesGetRes> getArticleList(HttpServletRequest request){

        String jwtToken = request.getHeader("Authorization");

        if(jwtToken == null){
            return ResponseEntity.status(401).body(ArticlesGetRes.of(401, "로그인이 필요합니다.", null, null));
        }

        String email = jwtTokenUtil.getUserEmailFromToken(jwtToken);

        //팔로우 여부 확인용
        User user = userService.getUserByEmail(email);

        List<Article> articles = communityService.getArticles();

        if(articles != null){
            return ResponseEntity.status(201).body(ArticlesGetRes.of(201, "정상적으로 작성되었습니다", articles, user));
        }else{
            return ResponseEntity.status(404).body(ArticlesGetRes.of(404, "조회에 실패하였습니다.", null, null));
        }
    }

    @PostMapping("/article/{articleId}/like")
    @ApiOperation(value = "좋아요", notes = "좋아여 성공 여부를 반환한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "좋아요 전환 성공"),
            @ApiResponse(code = 400, message = "좋아요 전환 실패"),
            @ApiResponse(code = 401, message = "로그인 필요")
    })
    public ResponseEntity<? extends LikePostRes> reverseLike(@PathVariable("articleId") Long articleId, @RequestBody LikePostReq likeInfo,
                                                             HttpServletRequest request){

        String jwtToken = request.getHeader("Authorization");

        if(jwtToken == null){
            return ResponseEntity.status(401).body(LikePostRes.of(401, "로그인이 필요합니다.", false));
        }

        String email = jwtTokenUtil.getUserEmailFromToken(jwtToken);

        boolean isSuccess = communityService.reverseArticleLike(email, articleId, likeInfo);


        if(isSuccess){
            return ResponseEntity.status(201).body(LikePostRes.of(201, "정상적으로 전환되었습니다", isSuccess));
        }else{
            return ResponseEntity.status(400).body(LikePostRes.of(400, "전환 실패", isSuccess));
        }
    }

    @PostMapping("/article/{articleId}/comment")
    @ApiOperation(value = "댓글 작성", notes = "작성된 댓글 id를 반환한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "댓글 작성 성공"),
            @ApiResponse(code = 400, message = "댓글 작성 실패"),
            @ApiResponse(code = 401, message = "로그인 필요")
    })
    public ResponseEntity<? extends CommentPostRes> createComment(@PathVariable("articleId") Long articleId,
                                                                  @RequestBody CommentCreatePostReq commentInfo,
                                                                  HttpServletRequest request){

        String jwtToken = request.getHeader("Authorization");

        if(jwtToken == null){
            return ResponseEntity.status(401).body(CommentPostRes.of(401, "로그인이 필요합니다.", 0L));
        }

        String email = jwtTokenUtil.getUserEmailFromToken(jwtToken);

        Comment comment = communityService.createComment(email, articleId, commentInfo);

        if(comment != null){
            return ResponseEntity.status(201).body(CommentPostRes.of(201, "정상적으로 작성되었습니다", comment.getId()));
        }else{
            return ResponseEntity.status(400).body(CommentPostRes.of(400, "댓글 작성에 실패했습니다.", 0L));
        }
    }

    @PutMapping("/article/{articleId}/comment")
    @ApiOperation(value = "댓글 수정", notes = "수정된 댓글 id 값을 응답한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "댓글 수정 성공"),
            @ApiResponse(code = 403, message = "댓글 수정 실패"),
            @ApiResponse(code = 401, message = "로그인 필요")
    })
    public ResponseEntity<? extends CommentPostRes> updateComment(@PathVariable("articleId") Long articleId,
                                                                  @RequestBody CommentPutReq commentInfo,
                                                                  HttpServletRequest request){

        String jwtToken = request.getHeader("Authorization");

        if(jwtToken == null){
            return ResponseEntity.status(401).body(CommentPostRes.of(401, "로그인이 필요합니다.", 0L));
        }

        String email = jwtTokenUtil.getUserEmailFromToken(jwtToken);

        Comment comment = communityService.updateComment(email, articleId, commentInfo);

        if(comment == null){
            return ResponseEntity.status(403).body(CommentPostRes.of(403, "수정이 불가능합니다.", 0L));
        }else{
            return ResponseEntity.status(201).body(CommentPostRes.of(201, "정상적으로 수정되었습니다", comment.getId()));
        }
    }

    @DeleteMapping("/article/{articleId}/comment")
    @ApiOperation(value = "댓글 삭제", notes = "삭제된 댓글 id 값을 응답한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "댓글 삭제 성공"),
            @ApiResponse(code = 500, message = "댓글 삭제 실패"),
            @ApiResponse(code = 401, message = "로그인 필요")
    })
    public ResponseEntity<? extends CommentDelRes> deleteComment(@PathVariable("articleId") Long articleId,
                                                                 @RequestBody CommentDelReq commentInfo,
                                                                 HttpServletRequest request){

        String jwtToken = request.getHeader("Authorization");

        if(jwtToken == null){
            return ResponseEntity.status(401).body(CommentDelRes.of(401, "로그인이 필요합니다.", false));
        }

        String email = jwtTokenUtil.getUserEmailFromToken(jwtToken);

        boolean isSuccess = communityService.deleteComment(email, commentInfo);

        if(isSuccess){
            return ResponseEntity.status(201).body(CommentDelRes.of(201, "정상적으로 삭제되었습니다", isSuccess));
        }else{
            return ResponseEntity.status(403).body(CommentDelRes.of(403, "삭제 실패", isSuccess));
        }
    }


    @GetMapping("/article/popular")
    @ApiOperation(value = "인기글 조회", notes = "인기 게시글 목록을 반환한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "인기글 조회 성공"),
            @ApiResponse(code = 404, message = "인기글 조회 실패")
    })
    public ResponseEntity<? extends PopularArticleGetRes> popularArticles(){

        List<Article> articles = popularService.getPopularArticles();

        if(articles != null){
            return ResponseEntity.status(201).body(PopularArticleGetRes.of(201, "정상적으로 조회되었습니다", articles));
        }else{
            return ResponseEntity.status(404).body(PopularArticleGetRes.of(404, "인기글 조회 실패", null));
        }
    }

    @GetMapping("/article/id")
    @ApiOperation(value = "게시글 id 조회", notes = "ssr을 위해 전체 게시글 id를 반환한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "id 조회 성공"),
            @ApiResponse(code = 404, message = "id 조회 실패")
    })
    public ResponseEntity<? extends DynamicArticlesRes> dynamicArticles(){

        List<Article> articles = communityService.getArticles();

        if(articles != null){
            return ResponseEntity.status(201).body(DynamicArticlesRes.of(201, "정상적으로 작성되었습니다", articles));
        }else{
            return ResponseEntity.status(404).body(DynamicArticlesRes.of(404, "게시글 조회 실패", null));
        }
    }

}
