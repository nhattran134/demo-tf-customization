class Customizations {
    public static void init() {       
        // This is used to deploy to the AWS Prod Account (home of UAT and Prod) environments
        WithAwsPlugin.withRole().init()
        
        // Plugin used to perform a 'terraform fmt -check=true -diff=true' command in the ValidationStage, if non-zero error code we dont proceed.
        ValidateFormatPlugin.init()
        TerraformFormatCommand.withRecursive()
                              .withDiff()
        
        // Environment variables
        TerraformEnvironmentStage
            .withGlobalEnv("DEV_AWS_ROLE_ARN",              "arn:aws:iam::677700034553:role/demo")
            .withGlobalEnv("QA_AWS_ROLE_ARN",               "arn:aws:iam::677700034553:role/demo")
            .withGlobalEnv("S3_BACKEND_REGION",             "ap-southeast-1")
            .withGlobalEnv("DEV_S3_BACKEND_BUCKET",         "cloudstate-nathantr")
            .withGlobalEnv("QA_S3_BACKEND_BUCKET",          "cloudstate-nathantr")
        
        // AWS Tagging Standard
        TagPlugin
            .withEnvironmentTag("Environment")
            .withTag("OwnerEmail", "me@nathantr.com")
            .withTag("PipelineRepo", getRepoHost() + "/" + getRepoSlug())
            .withTag("Team", "nathantr")
            .init()
        
        // Store state in S3
        S3BackendPlugin.init()
    }
    
    public static String getRepoSlug() {
        def parsedScmUrl = Jenkinsfile.instance.getParsedScmUrl()
        def organization = parsedScmUrl['organization']
        def repo = parsedScmUrl['repo']
        
        return "${organization}/${repo}"
    }
    
    public static String getRepoHost() {
        def parsedScmUrl = Jenkinsfile.instance.getParsedScmUrl()
        def protocol = parsedScmUrl['protocol']
        def domain = parsedScmUrl['domain']
        
        // We cannot post using the git protocol, change to https
        if (protocol == "git") {
            protocol = "https"
        }
        
        return "${protocol}://${domain}"
    }
}