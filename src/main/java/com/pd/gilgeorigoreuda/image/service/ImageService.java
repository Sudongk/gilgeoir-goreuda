package com.pd.gilgeorigoreuda.image.service;

import com.pd.gilgeorigoreuda.image.domain.ImageFile;
import com.pd.gilgeorigoreuda.image.ImageUploader;
import com.pd.gilgeorigoreuda.image.domain.S3ImageDeleteEvent;
import com.pd.gilgeorigoreuda.image.dto.response.ImagesResponse;
import com.pd.gilgeorigoreuda.image.exception.EmptyImageListException;
import com.pd.gilgeorigoreuda.image.exception.ExceedImageListSizeException;
import com.pd.gilgeorigoreuda.image.exception.InvalidImageUrlException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final int MAX_IMAGE_LIST_SIZE = 5;
    private static final int EMPTY_LIST_SIZE = 0;

    // TODO: "image.gilgeorigoreuda.com" 로 변경
    private static final String IMAGE_URL_HOST = "dz68kixxhuu4k.cloudfront.net";

    private final ImageUploader imageUploader;
    private final ApplicationEventPublisher publisher;

    public ImagesResponse save(final List<MultipartFile> images) {
        validateSizeOfImages(images);

        List<ImageFile> imageFiles = images.stream()
                .map(ImageFile::new)
                .toList();
        List<String> imageNames = uploadImages(imageFiles);

        return new ImagesResponse(imageNames);
    }

    private List<String> uploadImages(final List<ImageFile> imageFiles) {
        return imageUploader.uploadImages(imageFiles);
    }

    public void deleteSingleImage(final String imageUrl) {
        try {
            URL targetUrl = new URL(imageUrl);
            if (targetUrl.getHost().equals(IMAGE_URL_HOST)) {
                String targetName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                publisher.publishEvent(new S3ImageDeleteEvent(targetName));
            }
        } catch (final MalformedURLException e) {
            throw new InvalidImageUrlException();
        }
    }

    private void validateSizeOfImages(final List<MultipartFile> images) {
        if (images.size() > MAX_IMAGE_LIST_SIZE) {
            throw new ExceedImageListSizeException();
        }
        if (images.size() == EMPTY_LIST_SIZE) {
            throw new EmptyImageListException();
        }
    }

}
