package com.ohou.backend.service;

import com.ohou.backend.dto.product.OptionResponseDto;
import com.ohou.backend.dto.product.ProductResponseDto;
import com.ohou.backend.entity.*;
import com.ohou.backend.repository.OptionRepository;
import com.ohou.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;


    public ProductResponseDto getProductInfo(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (!product.isPresent()) {
            throw new NullPointerException("유효하지 않은 상품입니다.");
        }

        // 하위항목이 추가로 존재하는 option은 fetch에서 모든 값을 가져올 수 없기 때문에 한 번 더 조회를 해줘야 함.
        List<Option> optionList = optionRepository.findAllByProduct(product.get());
        // OptionList to OptionRespoonseDtoList
        List<OptionResponseDto> optionResponseDtoList = optionListToOptionResponseDtoList(optionList);
        // imgSrc 받아오기
        List<String> imgSrc = new ArrayList<>();
        for (ProductImages productImages : product.get().getProductImages()) {
            imgSrc.add(productImages.getImgSrc());
        }
        // details 받아오기
        List<String> details = new ArrayList<>();
        for (ProductDetailImages productDetailImages : product.get().getProductDetailImages()) {
            details.add(productDetailImages.getImgSrc());
        }

        return ProductResponseDto.builder()
                .brandName(product.get().getBrand())
                .title(product.get().getTitle())
                .reviewCount(product.get().getComment().size())
                .price(product.get().getPrice())
                .option(optionResponseDtoList)
                .images(imgSrc)
                .details(details)
                .build();
    }

    private List<OptionResponseDto> optionListToOptionResponseDtoList(List<Option> optionList) {
        List<OptionResponseDto> optionResponseDtoList = new ArrayList<>();
        for (Option option : optionList) {
            List<String> detailList = new ArrayList<>();
            for(OptionDetails optionDetails: option.getOptionDetails()){
                detailList.add(optionDetails.getDetail());
            }

            optionResponseDtoList.add(OptionResponseDto.builder()
                            .optionName(option.getOptionName())
                            .detail(detailList)
                            .build());
        }

        return optionResponseDtoList;
    }
}