package hu.bankblaze.bankblaze.service;

import hu.bankblaze.bankblaze.model.Corporate;
import hu.bankblaze.bankblaze.repo.CorporateRepository;
import hu.bankblaze.bankblaze.repo.QueueNumberRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class CorporateService {
    @Autowired
    private QueueNumberRepository queueNumberRepository;
    @Autowired
    private CorporateRepository corporateRepository;

    public List<Corporate> getAllCorporates() {
        return corporateRepository.findAll();
    }

    public int generateQueueNumber(int number) {
        int queueNumber = 0;
        switch (number) {
            case 1 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(21));
            case 2 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(22));
            case 3 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(23));
            case 4 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(24));
        }
        return queueNumber;
    }

    private int generateNumber(int number, int queueNumber) {
        switch (number) {
            case 1 -> {
                if (queueNumber < 2100 || queueNumber == 2200) {
                    queueNumber = 2100;
                }
            }
            case 2 -> {
                if (queueNumber < 2200 || queueNumber == 2300) {
                    queueNumber = 2300;
                }
            }
            case 3 -> {
                if (queueNumber < 2300 || queueNumber == 2400) {
                    queueNumber = 2300;
                }
            }
            case 4 -> {
                if (queueNumber < 2400 || queueNumber == 2500) {
                    queueNumber = 2400;
                }
            }
        }
        return ++queueNumber;
    }
}
