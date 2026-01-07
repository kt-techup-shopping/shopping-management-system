import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 10,            // 동시에 접속할 가상 유저 수
    duration: '100s',    // 테스트 지속 시간
};

export default function () {
    let res = http.get('http://host.docker.internal:8080/api/v1/products');
    check(res, {
        'ok': (r) => r.status === 200,
    });
    sleep(1);
}