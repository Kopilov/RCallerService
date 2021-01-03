ARG DOCKER_IMAGE=kopilov/rcallerservice:base
FROM $DOCKER_IMAGE

COPY .  /
RUN ./gradlew build && find build -name "rcallerservice*jar" -exec cp {} /tmp/rcallerservice.jar \; && cp /scripts/rcallerservice.sh /tmp

# Install necessary R modules from APT
#RUN apt-get install -yq r-base-dev r-cran-curl r-cran-devtools r-cran-forecast

# Install necessary R modules from CRAN
#RUN echo 'install.packages("forecTheta", repos="http://cran.r-project.org")' | R --no-save
#RUN echo 'install.packages("tsoutliers", repos="http://cran.r-project.org")' | R --no-save
#RUN echo 'install.packages("MAPA", repos="http://cran.r-project.org")' | R --no-save
#RUN echo 'install.packages("prophet", repos="http://cran.r-project.org")' | R --no-save

#RUN echo 'install.packages("fasster", repos="http://cran.r-project.org")' | R --no-save
##or
#RUN echo 'install.packages("fabletools", repos="http://cran.r-project.org")' | R --no-save
#RUN echo 'devtools::install_github("tidyverts/fasster")' | R --no-save


FROM $DOCKER_IMAGE
COPY --from=0 /tmp/rcallerservice.jar /tmp/rcallerservice.jar
COPY --from=0 /tmp/rcallerservice.sh /tmp/rcallerservice.sh

EXPOSE 8080

WORKDIR /tmp
CMD /tmp/rcallerservice.sh
