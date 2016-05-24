INSERT INTO RESPONSE_DATA VALUES(1, 'application/json', 200, '/hello.json', 'GET', '{    "type": "application/json",    "example": " {\"greeting\" : \"Hello World\" "}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- INSERT INTO URI_CACHE (URI, ACTION_TYPE, RESPONSE_DATA_ID) VALUES('/hello.json', 'GET', 1);