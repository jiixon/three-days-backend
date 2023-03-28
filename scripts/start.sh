#!/usr/bin/env bash

# start.sh
# 서버 구동을 위한 스크립트

source /home/ubuntu/codedeploy.sh

REPOSITORY=/home/ubuntu/app
PROJECT_NAME=three-days
ACTIVE_PROFILE=dev

echo "> Build 파일 복사"
echo "> cp $REPOSITORY/deploy/*.jar $REPOSITORY/"
cp $REPOSITORY/deploy/*.jar $REPOSITORY/

echo "> 새 어플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)
echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"
chmod +x $JAR_NAME

# 실행 중이면 종료
CURRENT_PID=$(pgrep -fl $PROJECT_NAME | grep java | awk '{print $1}')

if [ -z "$CURRENT_PID" ]; then
    echo "NOT RUNNING"
else
    echo "> kill -9 $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 5
fi
# 실행 중이면 종료

echo "> $JAR_NAME 실행"

echo "> $JAR_NAME 를 profile=$ACTIVE_PROFILE 로 실행합니다."
nohup java -jar \
    -Dspring.profiles.active=$ACTIVE_PROFILE \
    -Djasypt.encryptor.password=$JASYPT_ENCRYPTOR_PASSWORD \
    $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

