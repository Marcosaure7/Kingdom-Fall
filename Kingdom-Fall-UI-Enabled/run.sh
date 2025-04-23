#!/bin/bash
java \
    --module-path target/libs:~/javafx-sdk-21.0.7/lib \
    --add-modules javafx.controls,javafx.fxml \
    -Dprism.forceGPU=true \
    -Dprism.allowhidpi=false \
    -jar target/Kingdom-Fall-1.0-SNAPSHOT.jar
