FROM mowerr/ubuntu-base:18.04

# That's how you tell the image, which game server to download
ENV GAME=""

# Update the package and install all dependencies
RUN set -x && \
    dpkg --add-architecture i386 && \
    apt-get update && \
    DEBIAN_FRONTEND="noninteractive" apt-get install -y --no-install-recommends \ 
        cron \
        iproute2 \
        file \
        python3 \
        bsdmainutils \
        unzip \
        binutils \
        bc \
        jq \
        tmux \
        netcat \
        cpio \
        lib32gcc1 \
        lib32stdc++6 \
        libsdl2-2.0-0:i386 \
    && \
    # Cleanup
    apt-get clean autoclean && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

# Directory that will be used for LinuxGSM
ENV SERVERDIR="/data/server" \
    # Add server directory to PATH
    PATH=/data/server:$PATH

# Tell s6 overlay to wait longer for server to stop
ENV S6_KILL_FINISH_MAXTIME=300000

# Define volume for all 'runtime' files
VOLUME ["/data"]

# Monitor whether the server is up and running
HEALTHCHECK --interval=5m --start-period=5m CMD ["/healthcheck.sh"]

# Capy all required files into the image
COPY root/ /