package nmng108.microtube.mainservice.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.configuration.ObjectStoreConfiguration;
import nmng108.microtube.mainservice.dto.auth.UserDetailsDTO;
import nmng108.microtube.mainservice.dto.base.BaseResponse;
import nmng108.microtube.mainservice.dto.base.PagingResponse;
import nmng108.microtube.mainservice.dto.video.request.CreateVideoDTO;
import nmng108.microtube.mainservice.dto.video.request.SearchVideoDTO;
import nmng108.microtube.mainservice.dto.video.request.UpdateVideoDTO;
import nmng108.microtube.mainservice.dto.video.request.VideoUpdateType;
import nmng108.microtube.mainservice.dto.video.response.VideoDTO;
import nmng108.microtube.mainservice.entity.Video;
import nmng108.microtube.mainservice.exception.BadRequestException;
import nmng108.microtube.mainservice.exception.ForbiddenException;
import nmng108.microtube.mainservice.exception.InternalServerException;
import nmng108.microtube.mainservice.exception.UnauthorizedException;
import nmng108.microtube.mainservice.repository.ChannelRepository;
import nmng108.microtube.mainservice.repository.CommentRepository;
import nmng108.microtube.mainservice.repository.UserRelationVideoRepository;
import nmng108.microtube.mainservice.repository.VideoRepository;
import nmng108.microtube.mainservice.repository.projection.VideoWithChannelOwner;
import nmng108.microtube.mainservice.service.ObjectStoreService;
import nmng108.microtube.mainservice.service.UserService;
import nmng108.microtube.mainservice.service.VideoService;
import nmng108.microtube.mainservice.util.HelperMethods;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedInputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VideoServiceImpl implements VideoService {
    ObjectStoreConfiguration objectStoreConfiguration;
    ObjectStoreService objectStoreService;
    UserService userService;
    VideoRepository videoRepository;
    CommentRepository commentRepository;
    UserRelationVideoRepository userRelationVideoRepository;
    ChannelRepository channelRepository;

//    String processingDir;
//    String resultDir;

    public VideoServiceImpl(
            ObjectStoreConfiguration objectStoreConfiguration,
            ObjectStoreService objectStoreService,
            UserService userService,
            VideoRepository videoRepository,
            CommentRepository commentRepository,
            UserRelationVideoRepository userRelationVideoRepository,
            ChannelRepository channelRepository
//            @Value("${video.temp-dir:tmp/original-videos/}") String processingDir,
//            @Value("${video.result-dir:tmp/result-videos/}") String resultDir,
    ) {
        this.objectStoreConfiguration = objectStoreConfiguration;
        this.objectStoreService = objectStoreService;
        this.userService = userService;
        this.videoRepository = videoRepository;
        this.commentRepository = commentRepository;
        this.userRelationVideoRepository = userRelationVideoRepository;
        this.channelRepository = channelRepository;
//        this.processingDir = processingDir;
//        this.resultDir = resultDir;
    }

//    @PostConstruct
//    public void init() {
//        File file = new File(processingDir);
//
//        try {
//            Files.createDirectories(Paths.get(resultDir));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        if (!file.exists()) {
//            file.mkdir();
//            log.info("Folder Created: {}", file.getAbsolutePath());
//        } else {
//            log.info("Folder already created");
//        }
//
//    }

    @Override
    public Mono<BaseResponse<PagingResponse<VideoDTO>>> getAll(SearchVideoDTO dto) {
        return userService.getCurrentUser()
                .singleOptional()
                .map((userOptional) -> {
                    dto.setAccessingUserId(userOptional.map(UserDetailsDTO::getId).orElse(null));

                    return dto;
                })
                .then(Mono.fromCallable(() -> videoRepository.buildQuery(dto)))
                .flatMap((query) -> query.countTotal()
                        .filter((total) -> total > 0)
                        .flatMap((total) -> query.find()
                                .map((v) -> new VideoDTO(v, VideoDTO.Format.CONCISE)
                                        .setThumbnail(objectStoreService.getDownloadUrl(v.getThumbnail()))
                                        .setChannelAvatar(objectStoreService.getDownloadUrl(v.getChannelAvatar())))
                                .collectList()
                                .map((list) -> new PagingResponse<>(dto, total, list))
                        )
                        .switchIfEmpty(Mono.just(new PagingResponse<>()))
                        .map(BaseResponse::succeeded));
    }

    @Override
    public Mono<BaseResponse<VideoDTO>> getById(String id) {
        return retrieveVideo(id)
                .switchIfEmpty(Mono.error(new NoResourceFoundException("")))
                .map((v) -> new VideoDTO(v, VideoDTO.Format.DETAILS)
                        .setThumbnail(objectStoreService.getDownloadUrl(v.getThumbnail()))
                        .setUrl(objectStoreService.getDownloadUrl(v.getDestFilepath()))
                        .setChannelAvatar(objectStoreService.getDownloadUrl(v.getChannelAvatar())))
                .map(BaseResponse::succeeded);
    }

    @Override
    @Transactional
    public Mono<BaseResponse<VideoDTO>> create(CreateVideoDTO dto) {
        return userService.getCurrentUser().switchIfEmpty(Mono.error(UnauthorizedException::new))
                .flatMap((u) -> channelRepository.findByUserId(u.getId()))
                .switchIfEmpty(Mono.error(new ForbiddenException("Channel does not exist. Create it before uploading.")))
                .flatMap((c) -> {
                    var v = dto.toVideo();

                    v.setChannelId(c.getId());
//                    v.setStatus(Video.Status.CREATING);
                    // If generating code immediately
                    String code = HelperMethods.getRandomString(12);

                    v.setCode(code);
                    v.setStatus(Video.Status.CREATED);

                    return videoRepository.findByCode(v.getCode())
                            .handle((_, sink) -> {
                                log.error(STR."Generated a duplicate code '\{code}'");
                                sink.error(new InternalServerException(STR."Having unexpected error. Try again later."));
                            })
                            .then(videoRepository.save(v))
                            .doOnNext((_) -> channelRepository.increaseVideoCount(v.getId()).subscribeOn(Schedulers.boundedElastic()).subscribe());
                })
//                .publishOn(Schedulers.boundedElastic())
                // Generate video's code later
//                .doOnNext((v) -> {
//                    // Generate code
//                    String code = null;
//
//                    for (int i = 10; i < 20 && code == null; i++) {
//                        for (int j = 0; j < 15; j++) {
//                            code = HelperMethods.getRandomString(12, v.getChannelId());
//
//                            if (videoRepository.findByCode(code).block() == null) {
//                                break;
//                            } else {
//                                code = null;
//                            }
//                        }
//                    }
//
//                    if (code == null) {
//                        videoRepository.delete(v).subscribe();
//                    }
//
//                    v.setCode(code);
//                    videoRepository.save(v).subscribe();
//                })
                .mapNotNull((v) -> new VideoDTO(v, VideoDTO.Format.CONCISE))
                .mapNotNull(BaseResponse::succeeded);
    }

    @Override
    public Mono<Resource> getThumbnailFile(String id) {
        return retrieveVideo(id)
                .switchIfEmpty(Mono.error(new NoResourceFoundException("")))
                .publishOn(Schedulers.boundedElastic())
                .map((v) -> new BufferedInputStream(objectStoreService.getObject(
                        objectStoreConfiguration.getTemporaryBucketName(),
                        v.getThumbnail()
                )))
                .map(InputStreamResource::new);
    }

    @Override
    public Mono<Resource> getMasterFile(String id) {
        return retrieveVideo(id)
                .switchIfEmpty(Mono.error(new NoResourceFoundException("")))
                .filter((v) -> (v.getStatus() == Video.Status.READY) && StringUtils.hasText(v.getDestFilepath()))
                .switchIfEmpty(Mono.error(new BadRequestException("Video is not ready")))
                .publishOn(Schedulers.boundedElastic())
                .map((v) -> new BufferedInputStream(objectStoreService.getObject(
                        objectStoreConfiguration.getHlsBucketName(),
                        STR."\{v.getDestFilepath().substring(1)}/master.m3u8"
                )))
                .map(InputStreamResource::new);
    }

    @Override
    public Mono<Resource> getResolutionBasedFile(String id, String resolution, String filename) {
        return retrieveVideo(id)
                .switchIfEmpty(Mono.error(new NoResourceFoundException("")))
                .filter((v) -> (v.getStatus() == Video.Status.READY) && StringUtils.hasText(v.getDestFilepath()))
                .switchIfEmpty(Mono.error(new BadRequestException("Video is not ready")))
                .map((v) -> new BufferedInputStream(objectStoreService.getObject(
                        objectStoreConfiguration.getHlsBucketName(),
                        STR."\{v.getDestFilepath()}/\{resolution}/\{filename}"
                )))
                .map(InputStreamResource::new);
    }

//    @Override
//    @Transactional
//    @Deprecated
//    public Mono<BaseResponse<Void>> uploadVideo(long id, Mono<FilePart> filePartMono) {
////        String fileExtension = Optional.ofNullable(file.headers().getContentType())
////                .orElseThrow(() -> new InvalidMediaTypeException("", "No \"Content-Type\" provided"))
////                .getSubtype();
//        // TODO: add username + user's ID to filename
//        ;
//
//        return Mono.zip(
//                        videoRepository.findById(id).switchIfEmpty(Mono.error(new BadRequestException("Video does not exist"))),
//                        filePartMono,
//                        filePartMono.map((filePart) -> Paths.get(StringUtils.cleanPath(processingDir)).resolve(UUID.randomUUID() + "-" + filePart.filename()))
//                ).log()
//                .flatMap((tuple3) -> {
//                    FilePart filePart = tuple3.getT2();
//                    Path path = tuple3.getT3();
//
//                    return filePart.transferTo(path).thenReturn(tuple3);
//                })
//                .flatMap((tuple3) -> {
//                    Video video = tuple3.getT1();
//                    FilePart filePart = tuple3.getT2();
//                    Path path = tuple3.getT3();
//
//                    video.setOriginalFilename(filePart.filename());
//                    video.setTempFilepath(path.toFile().getAbsolutePath()/*.replace(":\\", "/")*/.replace("\\", "/"));
//
//                    return videoRepository.save(video);
//                })
//                .flatMap((v) -> processVideo(id))
//                .thenReturn(BaseResponse.succeeded())
//                ;
////        return videoRepository.findById(id).log()
////                .switchIfEmpty(Mono.error(new BadRequestException("Video does not exists")))
////                .zipWith(filePartMono.map((filePart) -> Paths.get(StringUtils.cleanPath(processingDir)).resolve(UUID.randomUUID() + "-" + filePart.filename())))
////                .flatMap((tuple2) -> filePartMono.flatMap((f) -> f.transferTo(tuple2.getT2()).thenReturn(tuple2)))
////                .flatMap((tuple2) -> {
////                    return filePartMono
////                            .map((filePart) -> {
////                                Video video = tuple2.getT1();
////                                Path path = tuple2.getT2();
////
////                                video.setOriginalFilename(filePart.filename());
////                                video.setTempFilepath(path.toFile().getAbsolutePath()/*.replace(":\\", "/")*/.replace("\\", "/"));
////
////                                return video;
////                            })
////                            .flatMap(videoRepository::save);
////                })
////                .flatMap((v) -> processVideo(id))
////                .thenReturn(BaseResponse.succeeded())
////                ;
//    }

/*
    @Deprecated
    private Mono<Void> processVideo(long videoId) {
//        String output360p = HSL_DIR + videoId + "/360p/";
//        String output720p = HSL_DIR + videoId + "/720p/";
//        String output1080p = HSL_DIR + videoId + "/1080p/";

        return videoRepository.findById(videoId).log()
                .zipWhen((v) -> {
                    Path tmpFilepath = Paths.get(v.getTempFilepath());
                    log.info("tmpFilepath: {}", tmpFilepath);

                    try {
//            Files.createDirectories(Paths.get(output360p));
//            Files.createDirectories(Paths.get(output720p));
//            Files.createDirectories(Paths.get(output1080p));

                        // ffmpeg command
                        Path outputPath = Paths.get(resultDir, tmpFilepath.toFile().getName());

                        Files.createDirectories(outputPath);

                        String outputPathStr = outputPath.toAbsolutePath().toString()*/
    /*.replace(":\\", "/")*//*
.replace("\\", "/");

//                        if (!outputPathStr.startsWith("/")) {
//                            outputPathStr = "/" + outputPathStr;
//                        }
                        log.info("outputPathStr: {}", outputPathStr);
//                        String ffmpegArgs = STR."""
//                             -i \{tmpFilepath}
//                            -c:v libx264 -c:a aac
//                            -map 0:v -map 0:a -s:v:0 640x360 -b:v:0 800k
//                            -map 0:v -map 0:a -s:v:1 1280x720 -b:v:1 2800k
//                            -map 0:v -map 0:a -s:v:2 1920x1080 -b:v:2 5000k
//                            -var_stream_map "v:0,a:0 v:1,a:0 v:2,a:0"
//                            -master_pl_name \{outputPathStr}/\{videoId}/master.m3u8
//                            -f hls -hls_time 10 -hls_list_size 0
//                            -hls_segment_filename "\{outputPathStr}/\{videoId}/v%v/fileSequence%d.ts"
//                            "\{outputPathStr}/\{videoId}/v%v/prog_index.m3u8"
//                            """;
//                        STR."""
//                             ffmpeg -i \{tmpFilepath}
//                              -filter_complex
//                               [0:v]split=3[v1][v2][v3];\\s[v1]scale=w=1920:h=1080[v1out];\\s[v2]scale=w=1280:h=720[v2out];\\s[v3]scale=w=854:h=480[v3out]
//                              -map [v1out] -c:v:0 libx264 -b:v:0 5000k -maxrate:v:0 5350k -bufsize:v:0 7500k
//                              -map [v2out] -c:v:1 libx264 -b:v:1 2800k -maxrate:v:1 2996k -bufsize:v:1 4200k
//                              -map [v3out] -c:v:2 libx264 -b:v:2 1400k -maxrate:v:2 1498k -bufsize:v:2 2100k
//                              -map a:0 -c:a aac -b:a:0 192k -ac 2
//                              -map a:0 -c:a aac -b:a:1 128k -ac 2
//                              -map a:0 -c:a aac -b:a:2 96k -ac 2
//                              -f hls
//                              -hls_time 10
//                              -hls_playlist_type vod
//                              -hls_flags independent_segments
//                              -hls_segment_type mpegts
//                              -hls_segment_filename \{outputPathStr}/stream_%v/data%03d.ts
//                              -master_pl_name master.m3u8
//                              -var_stream_map v:0,a:0\\sv:1,a:1\\sv:2,a:2
//                              \{outputPathStr}/stream_%v/playlist.m3u8
//                             """

                        String[] ffmpegArgs = {
                                "ffmpeg", "-v" */
    /*loglevel*//*
, "info",
                                "-i", STR."\{tmpFilepath}",
                                "-filter_complex", "[0:v]split=3[v1][v2][v3]; [v1]scale=w=1920:h=1080[v1out]; [v2]scale=w=1280:h=720[v2out]; [v3]scale=w=854:h=480[v3out]",
                                "-map", "[v1out]", "-c:v:0", "libx264", "-b:v:0", "5000k", "-maxrate:v:0", "5350k", "-bufsize:v:0", "7500k",
                                "-map", "[v2out]", "-c:v:1", "libx264", "-b:v:1", "2800k", "-maxrate:v:1", "2996k", "-bufsize:v:1", "4200k",
                                "-map", "[v3out]", "-c:v:2", "libx264", "-b:v:2", "1400k", "-maxrate:v:2", "1498k", "-bufsize:v:2", "2100k",
                                "-map", "a:0", "-c:a", "aac", "-b:a:0", "192k", "-ac", "2",
                                "-map", "a:0", "-c:a", "aac", "-b:a:1", "128k", "-ac", "2",
                                "-map", "a:0", "-c:a", "aac", "-b:a:2", "96k", "-ac", "2",
                                "-f", "hls",
                                "-hls_time", "10",
                                "-hls_playlist_type", "vod",
                                "-hls_flags", "independent_segments",
                                "-hls_segment_type", "mpegts",
                                "-master_pl_name", "master.m3u8",
                                "-var_stream_map", "v:0,a:0 v:1,a:1 v:2,a:2",
                                "-hls_segment_filename", STR."\{outputPathStr}/stream_%v/data%03d.ts",
                                STR."\{outputPathStr}/stream_%v/playlist.m3u8"
                        };

                        log.info(String.join(",", ffmpegArgs));
                        //file this command
                        ProcessBuilder processBuilder = new ProcessBuilder(ffmpegArgs
//                                Arrays.stream(ffmpegArgs.split("[ \n]+"))
//                                        .map((s) -> s.replaceAll("\\\\s", " "))
//                                        .toArray(String[]::new)
                        );

                        processBuilder.inheritIO();

                        Process process = processBuilder.start();
                        int exit = process.waitFor();
                        log.info(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));

                        if (exit != 0) {
//                            log.info(new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8));
                            throw new RuntimeException("video processing failed!!");
                        }

                        return Mono.just(outputPath);
                    } catch (IOException ex) {
                        throw new RuntimeException("Video processing fail!! " + ex.getMessage());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .publishOn(Schedulers.boundedElastic())
                .map((tuple2) -> {
                    Path outputPath = tuple2.getT2();
                    File originalMasterFile = outputPath.resolve("master.m3u8").toFile();
                    File destMasterFile = outputPath.resolve("dest-master.m3u8").toFile();

                    // Rename folders "stream_%v" with corresponding resolution numbers.
                    // Destination folder names & number of streams are temporarily fixed
                    try (
                            BufferedReader originalMasterFileReader = new BufferedReader(new FileReader(originalMasterFile));
                            FileWriter destMasterFileWriter = new FileWriter(destMasterFile);
                    ) {
                        String line;

                        // Rename folders in master file
                        while ((line = originalMasterFileReader.readLine()) != null) {
                            line = line.replaceAll("^stream_" + 0, "1080p")
                                    .replaceAll("^stream_" + 1, "720p")
                                    .replaceAll("^stream_" + 2, "480p");
                            destMasterFileWriter.write(line + "\r\n");
                        }

                        // Rename actual folders
                        outputPath.resolve("stream_0").toFile().renameTo(outputPath.resolve("1080p").toFile());
                        outputPath.resolve("stream_1").toFile().renameTo(outputPath.resolve("720p").toFile());
                        outputPath.resolve("stream_2").toFile().renameTo(outputPath.resolve("480p").toFile());
                    } catch (IOException ex) {
                        destMasterFile.delete();

                        throw new RuntimeException("Video processing fail! Reason: " + ex.getMessage());
                    }

                    if (!(originalMasterFile.delete() && destMasterFile.renameTo(originalMasterFile))) {
                        throw new RuntimeException("Could not rename folders");
                    }

                    return tuple2;
                })
                .flatMap((tuple2) -> {
                    var video = tuple2.getT1();
                    String destFilepath = tuple2.getT2().toFile().getAbsolutePath();

                    try {
                        Files.delete(Paths.get(video.getTempFilepath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    video.setTempFilepath(null);
                    video.setDestFilepath(destFilepath);

                    return videoRepository.save(video);
                })
                .then();
    }
*/

    @Override
    @Transactional
    public Mono<BaseResponse<VideoDTO>> updateInfo(String id, UpdateVideoDTO dto) {
        Mono<VideoWithChannelOwner> videoMono = retrieveVideo(id);
        Mono<UserDetailsDTO> userMono = userService.getCurrentUser().switchIfEmpty(Mono.error(UnauthorizedException::new));
        var updateType = dto.getUpdateType();

        return switch (updateType) {
            case VideoUpdateType.UPDATE_INFO -> videoMono
                    .filterWhen((v) -> userMono
                            .map((u) -> Objects.equals(v.getUserId(), u.getId())))
                    .switchIfEmpty(Mono.error(ForbiddenException::new))
                    .flatMap((v) -> {
                        dto.applyPatchUpdatesToEntity(v);

                        return videoRepository.save(v);
                    })
                    .map((v) -> new VideoDTO(v, VideoDTO.Format.DETAILS)
                            .setThumbnail(objectStoreService.getDownloadUrl(v.getThumbnail()))
                            .setUrl(objectStoreService.getDownloadUrl(v.getDestFilepath()))
                    )
                    .map(BaseResponse::succeeded);
            case LIKE, DISLIKE -> videoMono
                    .flatMap((v) -> userMono
                            .flatMap((u) -> {
                                int reactionNumber = (updateType == VideoUpdateType.LIKE) ? 1 : 2;

                                return userRelationVideoRepository.findByUserIdAndVideoId(u.getId(), v.getId())
                                        .flatMap((relation) -> {
                                            Mono<?> minusOldCount = (relation.getReaction() != null && relation.getReaction() != reactionNumber)
                                                    ? (updateType == VideoUpdateType.LIKE)
                                                    ? videoRepository.changeDislikeCount(v.getId(), false)
                                                    : videoRepository.changeLikeCount(v.getId(), false)
                                                    : Mono.empty();

                                            return minusOldCount
                                                    .then(videoRepository.updateRelationToUser(u.getId(), v.getId(), 0, reactionNumber, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()))
                                                    .filter((exists) -> exists)
                                                    ;
                                        })
                                        .switchIfEmpty(videoRepository.insertRelationToUser(u.getId(), v.getId(), 0, reactionNumber, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()))
                                        .then((updateType == VideoUpdateType.LIKE)
                                                ? videoRepository.changeLikeCount(v.getId(), true)
                                                : videoRepository.changeDislikeCount(v.getId(), true))
                                        .thenReturn(BaseResponse.succeeded());
                            }));
            case CANCEL_LIKE, CANCEL_DISLIKE -> videoMono
                    .flatMap((v) -> userMono
                            .flatMap((u) -> videoRepository.updateRelationToUser(u.getId(), v.getId(), 0, null, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()))
                            .then((updateType == VideoUpdateType.CANCEL_LIKE)
                                    ? videoRepository.changeLikeCount(v.getId(), false)
                                    : videoRepository.changeDislikeCount(v.getId(), false))
                            .thenReturn(BaseResponse.succeeded()));
            case INCREASE_VIEW -> videoMono
                    .doOnNext((v) -> videoRepository.increaseViewCount(v.getId()).subscribeOn(Schedulers.boundedElastic()).subscribe())
                    .thenReturn(BaseResponse.succeeded());
        };
    }

    @Override
    @Transactional
    public Mono<BaseResponse<Void>> delete(String identifiable) {
        return retrieveVideo(identifiable)
                .switchIfEmpty(Mono.error(new NoResourceFoundException("")))
                .zipWith(userService.getCurrentUser())
                .publishOn(Schedulers.boundedElastic())
                .doOnNext((tuple2) -> delete(tuple2.getT1(), tuple2.getT2())
                        .subscribeOn(Schedulers.boundedElastic())
                        .then(channelRepository.decreaseVideoCount(tuple2.getT1().getChannelId()))
                        .onErrorMap((e) -> {
                            log.error("Cannot delete video with ID={}. Reason: ", e.getMessage());
                            return e;
                        })
                        .subscribe()
                )
                .then(Mono.just(BaseResponse.succeeded()));
    }

    /**
     * This method aims to be run asynchronously to applying chains (typically running in doOnNext operator).
     *
     * @return
     */
    @Override
    public Mono<Void> delete(Video video, UserDetailsDTO user) {
        Mono<Void> deleteDestinationDir = Mono.defer(() -> Optional.ofNullable(video.getDestFilepath())
                .filter(StringUtils::hasText)
                .map((storedDirPath) -> {
                    String[] splitDirPath = storedDirPath.split(":");
                    String bucketName = splitDirPath[0];
                    String actualDirPath = splitDirPath[1];

                    return Flux.fromIterable(objectStoreService.removeObjectsWithPrefix(bucketName, actualDirPath))
                            .doOnNext((i) -> {
                                try {
                                    log.info("[Deletion error] object: {}, message: {}", i.get().objectName(), i.get().message());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collectList()
                            .filter(List::isEmpty)
                            .switchIfEmpty(Mono.error(new RuntimeException("There were errors while deleting files from object store. Video ID=" + video.getId())))
                            .then();
                })
                .orElse(Mono.empty()));
        Mono<Void> deleteOriginalFile = Mono.defer(() -> Optional.ofNullable(video.getTempFilepath())
                .filter(StringUtils::hasText)
                .map((storedFilePath) -> {
                    String[] splitDirPath = storedFilePath.split(":");
                    String bucketName = splitDirPath[0];
                    String actualFilePath = splitDirPath[1];

                    return objectStoreService.removeObjectAsync(bucketName, actualFilePath);
                })
                .map(Mono::fromCompletionStage)
                .orElse(Mono.empty()));
        Mono<Void> deleteThumbnail = Mono.defer(() -> Optional.ofNullable(video.getThumbnail())
                .filter(StringUtils::hasText)
                .map((storedFilePath) -> {
                    String[] splitDirPath = storedFilePath.split(":");
                    String bucketName = splitDirPath[0];
                    String actualFilePath = splitDirPath[1];

                    return objectStoreService.removeObjectAsync(bucketName, actualFilePath);
                })
                .map(Mono::fromCompletionStage)
                .orElse(Mono.empty()));

        return videoRepository.softDeleteById(video.getId(), user.getId(), ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime())
                .doOnSuccess((b) -> log.info("result of deletion: {}", b))
                .flatMap((_) -> Mono.when(deleteDestinationDir, deleteOriginalFile, deleteThumbnail))
                .then(commentRepository.deleteByVideoId(video.getId()))
                .then(videoRepository.delete(video)) // delete permanently
                .doOnTerminate(() -> log.info("Deleted video - ID={}", video.getId()))
                ;
    }

//    @Override
//    public Mono<Void> updateViewerBehavior(String id, VideoUpdateType action) {
//        Mono<?> doAction = switch (action) {
//            case INCREASE_VIEW ->
//            case LIKE, DISLIKE ->
//            case CANCEL_LIKE, CANCEL_DISLIKE ->
//        };
//
//        return doAction.then();
//    }

    /**
     * Notable detail: this method allows to see UNLISTED videos when searching for a list, so using this with attention.
     */
    @Override
    public Mono<VideoWithChannelOwner> retrieveVideo(String identifiable) {
//        ExtendedVideoRepositoryImpl.VideoQuery query = videoRepository.buildQuery();

        return Mono.fromCallable(() -> Long.parseLong(identifiable))
                .flatMap(videoRepository::findByIdOrCode)
                .onErrorResume(NumberFormatException.class, (_) -> videoRepository.findByCode(identifiable))
                .flatMap((v) -> userService.getCurrentUser().singleOptional().flatMap((userOptional) -> videoRepository.buildQuery(
                        SearchVideoDTO.builder()
                                .ids(Collections.singletonList(v.getId()))
                                .accessingUserId(userOptional.map(UserDetailsDTO::getId).orElse(null))
                                .allowedVisibility(List.of(Video.Visibility.PUBLIC.number, Video.Visibility.NOT_LISTED.number))
                                .build()
                ).find().elementAt(0)))
//                .flatMap((v) -> userService.getCurrentUser().map(u -> ))
                ;
    }
}
