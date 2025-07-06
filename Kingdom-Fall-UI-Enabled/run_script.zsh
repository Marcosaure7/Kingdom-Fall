#!/bin/zsh

# Script zsh pour exécuter le projet Java

# Vérification des prérequis
check_prerequisites() {
    if ! command -v java &> /dev/null; then
        echo "Erreur : Java n'est pas installé. Veuillez installer un JDK."
        exit 1
    fi
    if ! command -v javac &> /dev/null; then
        echo "Erreur : Le compilateur Java (javac) n'est pas trouvé. Veuillez installer un JDK."
        exit 1
    fi
    if ! command -v mvn &> /dev/null; then
        echo "Erreur : Maven n'est pas installé. Veuillez installer Maven."
        exit 1
    fi
    if [[ ! -f "pom.xml" ]]; then
        echo "Erreur : Fichier pom.xml non trouvé dans le répertoire courant."
        exit 1
    fi
}

# Fonction principale pour exécuter le projet
run_maven_project() {
    local main_class=$1

    echo "=== Vérification des prérequis ==="
    check_prerequisites

    echo "=== Nettoyage du projet ==="
    mvn clean
    if [[ $? -ne 0 ]]; then
        echo "Erreur lors du nettoyage du projet."
        exit 1
    fi

    echo "=== Compilation du projet ==="
    mvn compile
    if [[ $? -ne 0 ]]; then
        echo "Erreur lors de la compilation du projet."
        exit 1
    fi

    echo "=== Exécution des tests ==="
    mvn test
    if [[ $? -ne 0 ]]; then
        echo "Attention : Certains tests ont échoué."
        # On continue même si les tests échouent, ajustez si nécessaire
    fi

    echo "=== Construction du projet ==="
    mvn package
    if [[ $? -ne 0 ]]; then
        echo "Erreur lors de la construction du projet."
        exit 1
    fi

    echo "=== Exécution de l'application ==="
    mvn exec:java -Dexec.mainClass="application.App" -Dexec.args="--module-path /lib/javafx/javafx-sdk-21.0.7/lib --add-modules javafx.controls,javafx.fxml -Dprism.forceGPU=true -Dprism.allowhidpi=false"
    if [[ $? -ne 0 ]]; then
        echo "Erreur lors de l'exécution de l'application."
        exit 1
    fi
}

run_maven_project

echo "=== Opération terminée ==="
