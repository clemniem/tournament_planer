# /bin/bash
docker-compose stop
SCRIPTPATH=$(dirname "$SCRIPT")
cd $SCRIPTPATH
git pull
cd hatplaner
sbt docker:publishLocal
cd ../hat-play
sbt docker:publishLocal
cd ../TournamentScheduler
sbt docker:publishLocal
docker-compose up -d

