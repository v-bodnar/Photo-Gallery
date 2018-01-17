package net.omb.photogallery.services;

import net.omb.photogallery.model.Tag;
import net.omb.photogallery.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public List<Tag> findAndSaveIfNotExist(List<String> names){
        List<Tag> tags = tagRepository.findByNames(names);
        List<String> existingTags = tags.stream().map(tag -> tag.getName()).collect(Collectors.toList());
        names.removeAll(existingTags);
        List<Tag> tagsToSave = new ArrayList<>();
        for(String name : names){
            tagsToSave.add(new Tag(name));
        }
        tags.addAll((List<Tag>)tagRepository.saveAll(tagsToSave));
        return tags;
    }


}
