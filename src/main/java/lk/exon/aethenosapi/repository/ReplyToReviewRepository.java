package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.ReplyToReview;
import lk.exon.aethenosapi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyToReviewRepository extends JpaRepository<ReplyToReview, Integer> {
    List<ReplyToReview> getReplyToReviewByReview(Review review);
}
