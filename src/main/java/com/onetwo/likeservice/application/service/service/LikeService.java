package com.onetwo.likeservice.application.service.service;

import com.onetwo.likeservice.application.port.in.command.RegisterLikeCommand;
import com.onetwo.likeservice.application.port.in.response.RegisterLikeResponseDto;
import com.onetwo.likeservice.application.port.in.usecase.RegisterLikeUseCase;
import com.onetwo.likeservice.application.port.out.ReadLikePort;
import com.onetwo.likeservice.application.port.out.RegisterLikePort;
import com.onetwo.likeservice.application.service.converter.LikeUseCaseConverter;
import com.onetwo.likeservice.common.exceptions.ResourceAlreadyExistsException;
import com.onetwo.likeservice.domain.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService implements RegisterLikeUseCase {

    private final RegisterLikePort registerLikePort;
    private final ReadLikePort readLikePort;
    private final LikeUseCaseConverter likeUseCaseConverter;

    /**
     * Register like use case,
     * register like data to persistence
     *
     * @param registerLikeCommand data about register like with user id and target id
     * @return Boolean about register success
     */
    @Override
    @Transactional
    public RegisterLikeResponseDto registerLike(RegisterLikeCommand registerLikeCommand) {
        Optional<Like> optionalLike = readLikePort.findByUserIdAndCategoryAndTargetId(registerLikeCommand);

        if (optionalLike.isPresent())
            throw new ResourceAlreadyExistsException("like already exist. user can like target only ones");

        Like newLike = Like.createNewLikeByCommand(registerLikeCommand);

        Like savedLike = registerLikePort.registerLike(newLike);

        return likeUseCaseConverter.likeToRegisterResponseDto(savedLike);
    }
}
