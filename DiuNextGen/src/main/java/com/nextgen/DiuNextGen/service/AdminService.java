package com.nextgen.DiuNextGen.service;
import com.nextgen.DiuNextGen.dto.RoundRequest;
import com.nextgen.DiuNextGen.dto.TopicRequest;
import com.nextgen.DiuNextGen.entity.Round;
import com.nextgen.DiuNextGen.entity.Topic;
import com.nextgen.DiuNextGen.repository.RoundRepository;
import com.nextgen.DiuNextGen.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class AdminService {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private RoundRepository roundRepository;
    public Topic createTopic(TopicRequest request) {
        if (topicRepository.existsByName(request.getName())) {
            throw new RuntimeException("Topic already exists!");
        }
        Topic topic = new Topic();
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        return topicRepository.save(topic);
    }
    public Round createRound(RoundRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found!"));
        Round round = new Round();
        round.setName(request.getName());
        round.setVjudgeContestId(request.getVjudgeContestId());
        round.setTopic(topic);
        return roundRepository.save(round);
    }
    @Autowired
    private com.nextgen.DiuNextGen.repository.UserRepository userRepository; // Add this

}
