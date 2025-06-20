import http from 'k6/http';
import { check } from 'k6';

export let options = {
  vus: 100, //concurrent users per sec
  duration: '60s', //run for 60s
};

export default function () {
  const id = `task-${__VU}-${__ITER}`;
  const res = http.post(`http://host.docker.internal:8080/tasks?taskId=${id}&duration=1000`);

  check(res, {
    'status is 201': (r) => r.status === 201,
  });
}
