package com.hopeuniversity.hope_university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResult {
    private int created;
    private List<String> errors;
}

