package com.ssafy.api.service;

import com.ssafy.api.request.DislikePostReq;
import com.ssafy.api.request.LetterPostReq;
import com.ssafy.api.response.KindRes;
import com.ssafy.api.response.RecommendResultRes;
import com.ssafy.api.response.RecommendTagRes;
import com.ssafy.db.entity.*;
import com.ssafy.db.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RecommendService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    KindRepository kindRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    FlowerDislikeRepository flowerDislikeRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    LetterRepository letterRepository;
    @Autowired
    ArticleRepository articleRepository;

    @Value("${django.redirect.uri}")
    private String DJANGO_REDIRECT_URI;

    @Transactional
    public boolean addDislike(String email, DislikePostReq dislikeInfo){

        User user = userRepository.findByEmail(email);

        FlowerDislike dislike = new FlowerDislike();

        dislike.setKind(kindRepository.findById(dislikeInfo.getKindId()).get());
        dislike.setUser(user);

        flowerDislikeRepository.save(dislike);

        return true;
    }

    public List<RecommendTagRes> recommendBySituation(String email){

        User user = userRepository.findByEmail(email);

        int tagSize = (int)tagRepository.count();

        List<RecommendTagRes> tags = new ArrayList<>();
        for(int i=1; i<= tagSize; i++){
            long tagId = Long.valueOf(i);

            List<Long> kindIds = connectSituation(user.getId(), tagId).getResult();

            RecommendTagRes tagRes = new RecommendTagRes();
            Tag tag = tagRepository.findById(tagId).get();
            tagRes.setTagId(tag.getId());
            tagRes.setTagName(tag.getDear());

            List<KindRes> flowers = new ArrayList<>();
            for(Long id : kindIds){
                KindRes kindRes = new KindRes();
                Kind kind = kindRepository.findById(id).get();
                kindRes.setKindId(id);
                kindRes.setKindImage(kind.getFlowerImage());
                kindRes.setSubjectId(kind.getSubject().getId());

                flowers.add(kindRes);
            }

            tagRes.setFlowers(flowers);
            tags.add(tagRes);
        }

        return tags;
    }

    public List<Article> recommendByLike(String email){

        //TODO : 장고에게 유저 id 전달, article id list 받아오기
        User user = userRepository.findByEmail(email);

        List<Long> articleIds = new ArrayList<>();

        List<Article> articles = new ArrayList<>();
        for(Long articleId : articleIds){
            articles.add(articleRepository.findById(articleId).get());
        }

        return articles;
    }

    @Transactional
    public Subject recommendByLetter(LetterPostReq letterInfo){
        //TODO : 장고에게 letterInfo 전달, subjectId 받아오기
        Long subjectId = 1L;

        Letter letter = new Letter();
        letter.setContent(letterInfo.getContent());

        Subject subject = subjectRepository.findById(subjectId).get();
        letter.setSubject(subject);

        letterRepository.save(letter);

        return subject;

    }

    public RecommendResultRes connectSituation(Long userId, Long tagId){
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromUriString(DJANGO_REDIRECT_URI)
                .path("/api/data/tag/{userId}/{tagId}")
                .encode()
                .build()
                .expand(userId, tagId)
                .toUri();

        ResponseEntity<RecommendResultRes> kindIds = restTemplate.getForEntity(uri, RecommendResultRes.class);

        return kindIds.getBody();
    }

}
