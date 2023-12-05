package hu.bankblaze.bankblaze.service;

import hu.bankblaze.bankblaze.model.Retail;
import hu.bankblaze.bankblaze.repo.QueueNumberRepository;
import hu.bankblaze.bankblaze.repo.RetailRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RetailService {

    @Autowired
    private RetailRepository retailRepository;
    private QueueNumberRepository queueNumberRepository;

    public List<Retail> getAllRetail () {
        return retailRepository.findAll();
    }

    public int generateQueueNumber(int number) {
        int queueNumber = 0;
        switch (number) {
            case 1 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(11));
            case 2 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(12));
            case 3 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(13));
            case 4 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(14));
        }
        return queueNumber;
    }

    private int generateNumber(int number, int queueNumber) {
        switch (number) {
            case 1 -> {
                if (queueNumber < 1100 || queueNumber == 1200) {
                    queueNumber = 1100;
                }
            }
            case 2 -> {
                if (queueNumber < 1200 || queueNumber == 1300) {
                    queueNumber = 1300;
                }
            }
            case 3 -> {
                if (queueNumber < 1300 || queueNumber == 1400) {
                    queueNumber = 1300;
                }
            }
            case 4 -> {
                if (queueNumber < 1400 || queueNumber == 1500) {
                    queueNumber = 1400;
                }
            }
        }
        return ++queueNumber;
    }

}
