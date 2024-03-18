package edu.java.scrapper.client.stackoverflow;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StackOverflowJsonResponse {

    public static final String QUESTION_RESPONSE_BODY = """
        {
            "items": [
                {
                    "tags": [
                        "c",
                        "data-structures",
                        "linked-list"
                    ],
                    "answer_count": 17,
                    "last_activity_date": 1708698398,
                    "question_id": 12345,
                    "link": "https://stackoverflow.com/questions/12345/test_for_hw2"
                }
            ],
            "has_more": true,
            "quota_max": 10000,
            "quota_remaining": 9934
        }
        """;

    public static final String COMMENTS_RESPONSE_BODY = """
        {
          "items": [
            {
              "owner": {
                "account_id": 7799666
              },
              "edited": false,
              "score": 1,
              "comment_id": 110156174,
              "content_license": "CC BY-SA 4.0"
            },
            {
              "owner": {
                "account_id": 2712119
              },
              "edited": false,
              "score": 8,
              "comment_id": 96880783,
              "content_license": "CC BY-SA 4.0"
            }
          ],
          "has_more": false,
          "quota_max": 10000,
          "quota_remaining": 9979
        }
        """;
}
