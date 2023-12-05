package hu.bankblaze.bankblaze.service;

import hu.bankblaze.bankblaze.model.Teller;
import hu.bankblaze.bankblaze.repo.QueueNumberRepository;
import hu.bankblaze.bankblaze.repo.TellerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class TellerService {

    @Autowired
    private final TellerRepository tellerRepository;
    @Autowired
    private final QueueNumberRepository queueNumberRepository;

    public List<Teller> getAllTellers() {
        return tellerRepository.findAll();
    }

    public int generateQueueNumber(int number) {
        int queueNumber = 0;
        switch (number) {
            case 1 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(31));
            case 2 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(32));
            case 3 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(33));
            case 4 -> queueNumber = generateNumber(number, queueNumberRepository.getLastNumber(34));
        }
        return queueNumber;
    }

    private int generateNumber(int number, int queueNumber) {
        switch (number) {
            case 1 -> {
                if (queueNumber < 3100 || queueNumber == 3200) {
                    queueNumber = 3100;
                }
            }
            case 2 -> {
                if (queueNumber < 3200 || queueNumber == 3300) {
                    queueNumber = 3200;
                }
            }
            case 3 -> {
                if (queueNumber < 3300 || queueNumber == 3400) {
                    queueNumber = 3300;
                }
            }
            case 4 -> {
                if (queueNumber < 3400 || queueNumber == 3500) {
                    queueNumber = 3400;
                }
            }
        }
        return ++queueNumber;
    }
}
