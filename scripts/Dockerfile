ARG DOCKER_IMAGE=gitlab.dellin.ru:5005/omoim_developers/javadocker/adoptopenjdk:11.0.6_10-jdk-hotspot-eoan
FROM $DOCKER_IMAGE

# Install R from CRAN
RUN apt-get -y update && apt-get install -y curl r-base r-base-dev r-cran-curl r-cran-devtools r-cran-forecast

RUN echo 'install.packages("forecTheta", repos="http://cran.r-project.org")' | R --no-save
RUN echo 'install.packages("tsoutliers", repos="http://cran.r-project.org")' | R --no-save
RUN echo 'install.packages("MAPA", repos="http://cran.r-project.org")' | R --no-save
RUN echo 'install.packages("prophet", repos="http://cran.r-project.org")' | R --no-save

#RUN echo 'install.packages("fasster", repos="http://cran.r-project.org")' | R --no-save
##or
#RUN echo 'install.packages("fabletools", repos="http://cran.r-project.org")' | R --no-save
#RUN echo 'devtools::install_github("tidyverts/fasster")' | R --no-save

COPY rcallerservice.sh /tmp
COPY rcallerservice.jar /tmp
EXPOSE 8080

WORKDIR /tmp
CMD /tmp/rcallerservice.sh
