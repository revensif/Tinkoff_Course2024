package edu.java.service;

import edu.java.dto.Link;
import edu.java.updates.UpdatesInfo;

public interface UpdateChecker {

    boolean supports(Link link);

    void checkForUpdates(UpdatesInfo updatesInfo, String[] pathParts, long linkId);
}
