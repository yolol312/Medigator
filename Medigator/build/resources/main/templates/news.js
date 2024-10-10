require('dotenv').config();
const express = require('express');
const request = require('request');
const app = express();

const CLIENT_ID = process.env.CLIENT_ID;     // 환경변수에서 클라이언트 ID 로드
const CLIENT_SECRET = process.env.CLIENT_SECRET; // 환경변수에서 클라이언트 시크릿 로드

app.get('/search/blog', function (req, res) {
    const query = encodeURI(req.query.query); // 클라이언트로부터 받은 검색어 인코딩
    const apiURL = `https://openapi.naver.com/v1/search/blog?query=${query}`;

    const options = {
        url: apiURL,
        headers: {
            'X-Naver-Client-Id': CLIENT_ID,
            'X-Naver-Client-Secret': CLIENT_SECRET
        }
    };

    request.get(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            res.writeHead(200, {'Content-Type': 'text/json;charset=utf-8'});
            res.end(body);
        } else {
            console.error('Error:', error);
            console.error('Status Code:', response.statusCode);
            res.status(response.statusCode).end();
        }
    });
});

const port = process.env.PORT || 3000;
app.listen(port, function () {
    console.log(`Server running on http://localhost:${port}/search/blog?query=검색어`);
});