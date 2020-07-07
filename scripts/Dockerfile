ARG DOCKER_IMAGE=ubuntu:focal
FROM $DOCKER_IMAGE

#Configure Ubuntu
RUN apt-get update && apt-get -y upgrade && apt-get install -yq locales tzdata software-properties-common apt-utils curl wget

#change to ru_RU.UTF-8 etc
RUN locale-gen "en_US.UTF-8"
RUN echo "LANG=en_US.UTF-8" >> /etc/default/locale

#Change to "Europe/Moscow" etc
RUN echo "UTC" > /etc/timezone
RUN rm -f /etc/localtime
RUN dpkg-reconfigure -f noninteractive tzdata

# Install Java
RUN apt-get install -yq openjdk-11-jre-headless
# Install R
RUN apt-get install -yq r-base
# Install necessary R modules from APT
RUN apt-get install -yq r-base-dev r-cran-curl r-cran-devtools r-cran-forecast

# Install necessary R modules from CRAN
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
