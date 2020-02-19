ARG DOCKER_IMAGE=gitlab.dellin.ru:5005/omoim_developers/javadocker/centos/java_build:2019-08-01_17-08
FROM $DOCKER_IMAGE

COPY epel.repo /etc/yum.repos.d

RUN yum update -y && yum install -y R curl libcurl-devel
RUN mkdir -p /usr/share/doc/R-3.6.0/html && cp /usr/lib64/R/library/stats/html/R.css /usr/share/doc/R-3.6.0/html/

RUN echo 'install.packages("curl", repos="http://cran.r-project.org")' | R --no-save
RUN echo 'install.packages("forecast", repos="http://cran.r-project.org")' | R --no-save
RUN echo 'install.packages("forecTheta", repos="http://cran.r-project.org")' | R --no-save
RUN echo 'install.packages("tsoutliers", repos="http://cran.r-project.org")' | R --no-save
RUN echo 'install.packages("prophet", repos="http://cran.r-project.org")' | R --no-save
RUN echo 'install.packages("fasster", repos="http://cran.r-project.org")' | R --no-save
RUN echo 'install.packages("MAPA", repos="http://cran.r-project.org")' | R --no-save

RUN yum remove -y libcurl-devel && yum autoremove -y

COPY rcallerservice.sh /tmp
COPY rcallerservice.jar /tmp
EXPOSE 8080

WORKDIR /tmp
CMD /tmp/rcallerservice.sh
