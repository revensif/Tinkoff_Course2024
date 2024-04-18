package edu.java.service;

import edu.java.dto.Link;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdaterFetcher {

    private final List<UpdateChecker> updateCheckers;

    public LinkUpdaterFetcher(List<UpdateChecker> updateCheckers) {
        this.updateCheckers = updateCheckers;
    }

    public UpdateChecker getUpdateChecker(Link link) {
        for (UpdateChecker checker : updateCheckers) {
            if (checker.supports(link)) {
                return checker;
            }
        }
        return null;
    }
}
