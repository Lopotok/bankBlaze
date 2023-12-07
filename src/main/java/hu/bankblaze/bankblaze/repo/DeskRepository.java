package hu.bankblaze.bankblaze.repo;

import hu.bankblaze.bankblaze.model.Desk;
import hu.bankblaze.bankblaze.model.QueueNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeskRepository extends JpaRepository<Desk, Long> {
    Desk findByEmployeeId(Long loggedInUserId);

    Desk findByQueueNumber(QueueNumber queueNumber);

    int countByQueueNumberIsNotNullAndQueueNumberToRetailIsTrue();

    int countByQueueNumberIsNotNullAndQueueNumberToCorporateIsTrue();

    int countByQueueNumberIsNotNullAndQueueNumberToTellerTrue();

    int countByQueueNumberIsNotNullAndQueueNumberToPremiumIsTrue();

}

