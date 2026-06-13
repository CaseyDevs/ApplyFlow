-- Drop the existing foreign key constraint on job_board_application.application_id
ALTER TABLE job_board_application DROP CONSTRAINT fkt974qraimxgxm7vebvs75qai8;

-- Recreate the foreign key with ON DELETE SET NULL
ALTER TABLE job_board_application 
ADD CONSTRAINT fkt974qraimxgxm7vebvs75qai8 
FOREIGN KEY (application_id) REFERENCES application(id) ON DELETE SET NULL;
