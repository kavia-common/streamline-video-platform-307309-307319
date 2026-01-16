#!/bin/bash
cd /home/kavia/workspace/code-generation/streamline-video-platform-307309-307319/video_streaming_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

